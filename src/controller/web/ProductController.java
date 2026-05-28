package controller.web;

import controller.web.dto.*;
import domain.*;
import infra.web.Router;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductController {

    private final ProductList  productList;
    private final RiderList    riderList;
    private final CoverageList coverageList;

    private static final long   STANDARD_BASE_PREMIUM = 1_150_000L;
    private static final double LEGAL_RESERVE_RATIO   = 0.40;

    public ProductController(ProductList productList, RiderList riderList, CoverageList coverageList) {
        this.productList  = productList;
        this.riderList    = riderList;
        this.coverageList = coverageList;
    }

    public void registerRoutes(Router router) {
        router.get("/products",                          (req, res) -> res.ok(getAll(req.queryParam("onSale"))));
        router.get("/products/{id}",                     (req, res) -> res.ok(ProductResponse.from(productList.getById(req.pathVariable("id")))));
        router.post("/products",                         (req, res) -> res.created(create(req.body(ProductCreateRequest.class))));
        router.post("/products/{id}/estimate",           (req, res) -> res.ok(estimate(req.pathVariable("id"), req.body(PremiumEstimateRequest.class))));
        router.post("/products/{id}/premium-calculation",(req, res) -> res.ok(premiumCalculation(req.pathVariable("id"), req.body(PremiumCalcRequest.class))));
        router.put("/products/{id}/approval",            (req, res) -> res.ok(applyForApproval(req.pathVariable("id"))));
        router.put("/products/{id}/rate-verification",   (req, res) -> res.ok(applyRateVerification(req.pathVariable("id"))));
        router.put("/products/{id}/sale",                (req, res) -> res.ok(confirmSale(req.pathVariable("id"))));
        router.get("/coverages",                         (req, res) -> res.ok(getAllCoverages()));
        router.get("/riders",                            (req, res) -> res.ok(getAllRiders()));
    }

    private List<ProductResponse> getAll(String onSale) {
        ProductList all = productList.findAll();
        return ("true".equals(onSale) ? all.onSaleOnly() : all)
                .getAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    private ProductResponse create(ProductCreateRequest req) {
        Product product = Product.design(
                req.productCode(), req.productName(),
                req.description(), Target.valueOf(req.target()),
                parseDate(req.saleStartDate()), parseDate(req.saleEndDate())
        );
        if (req.riderCodes() != null) {
            List<ProductRider> riders = req.riderCodes().stream()
                    .map(riderList::findByCode)
                    .filter(r -> r != null)
                    .map(ProductRider::from)
                    .collect(Collectors.toList());
            product.setRiders(riders);
        }
        productList.save(product);
        return ProductResponse.from(product);
    }

    private PremiumEstimateResponse estimate(String id, PremiumEstimateRequest req) {
        productList.validateExists(id);
        CarPurpose purpose = CarPurpose.valueOf(req.carPurpose());
        PremiumCalculation calc = PremiumCalculation.calculate(
                req.carStandardValue(), purpose, Collections.emptyList()
        );
        return new PremiumEstimateResponse(calc.getSubtotal(), calc.getFinalPremium(), "KRW");
    }

    private ProductResponse applyForApproval(String id) {
        Product p = productList.getById(id);
        p.applyForApproval();
        productList.save(p);
        return ProductResponse.from(p);
    }

    private ProductResponse applyRateVerification(String id) {
        Product p = productList.getById(id);
        p.applySalePermit();
        productList.save(p);
        return ProductResponse.from(p);
    }

    private ProductResponse confirmSale(String id) {
        Product p = productList.getById(id);
        p.onsale();
        productList.save(p);
        return ProductResponse.from(p);
    }

    private List<Map<String, Object>> getAllCoverages() {
        return coverageList.findAll().getAll().stream()
                .map(c -> {
                    List<String> options = c.getLimitOptions() != null
                            ? c.getLimitOptions().stream()
                                .map(o -> o.getOptionName())
                                .collect(Collectors.toList())
                            : List.of();
                    return Map.of(
                            "coverageId",   (Object) c.getCoverageId(),
                            "coverageName", c.getCoverageName(),
                            "mandatory",    c.isMandatory(),
                            "limitOptions", options);
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getAllRiders() {
        return riderList.findAll().getAll().stream()
                .map(r -> Map.of(
                        "riderCode",    (Object) r.getRiderCode(),
                        "riderName",    r.getRiderName(),
                        "discountRate", r.getDiscountRate() != null ? r.getDiscountRate() : 0.0))
                .collect(Collectors.toList());
    }

    private Map<String, Object> premiumCalculation(String id, PremiumCalcRequest req) {
        productList.validateExists(id);
        long finalPremium   = STANDARD_BASE_PREMIUM;
        long netPremium     = Math.round(finalPremium * req.lossRatio() / 100.0);
        long expensePremium = finalPremium - netPremium;
        long reserve        = Math.round(finalPremium * LEGAL_RESERVE_RATIO);
        long totalRevenue   = finalPremium * req.targetSales();
        long totalClaims    = Math.round(totalRevenue * req.lossRatio() / 100.0);
        long totalExpenses  = Math.round(totalRevenue * (req.salesExpense() + req.adminExpense()) / 100.0);
        long profit         = totalRevenue - totalClaims - totalExpenses;
        return Map.of(
                "finalPremium",   finalPremium,
                "reserve",        reserve,
                "netPremium",     netPremium,
                "expensePremium", expensePremium,
                "totalRevenue",   totalRevenue,
                "totalClaims",    totalClaims,
                "totalExpenses",  totalExpenses,
                "profit",         profit
        );
    }

    public record PremiumCalcRequest(
            long   targetSales,
            double lossRatio,
            double salesExpense,
            double adminExpense
    ) {}

    private static Date parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(s); }
        catch (ParseException e) { return null; }
    }
}
