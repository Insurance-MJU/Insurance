package controller.web;

import controller.web.dto.*;
import domain.*;
import infra.web.Router;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ProductController {

    private final ProductList productList;
    private final RiderList riderList;

    public ProductController(ProductList productList, RiderList riderList) {
        this.productList = productList;
        this.riderList = riderList;
    }

    public void registerRoutes(Router router) {
        router.get("/products",                      (req, res) -> res.ok(getAll(req.queryParam("onSale"))));
        router.get("/products/{id}",                 (req, res) -> res.ok(ProductResponse.from(productList.getById(req.pathVariable("id")))));
        router.post("/products",                     (req, res) -> res.created(create(req.body(ProductCreateRequest.class))));
        router.post("/products/{id}/estimate",       (req, res) -> res.ok(estimate(req.pathVariable("id"), req.body(PremiumEstimateRequest.class))));
        router.put("/products/{id}/approval",        (req, res) -> res.ok(applyForApproval(req.pathVariable("id"))));
        router.put("/products/{id}/rate-verification",(req, res) -> res.ok(applyRateVerification(req.pathVariable("id"))));
        router.put("/products/{id}/sale",            (req, res) -> res.ok(confirmSale(req.pathVariable("id"))));
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

    private static Date parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(s); }
        catch (ParseException e) { return null; }
    }
}
