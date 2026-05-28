package controller.web;

import common.exception.infra.ForbiddenException;
import controller.web.dto.*;
import domain.*;
import infra.web.Router;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        // CS-02: 상품 목록 조회
        router.get("/products", (req, res) -> {
            String onSale = req.queryParam("onSale");
            ProductList all = productList.findAll();
            List<ProductResponse> result = ("true".equals(onSale) ? all.onSaleOnly() : all)
                    .getAll().stream()
                    .map(ProductResponse::from)
                    .collect(Collectors.toList());
            res.ok(result);
        });

        // CS-02: 상품 상세 조회
        router.get("/products/{id}", (req, res) -> {
            Product p = productList.findById(req.pathVariable("id"));
            if (p == null) { res.error(404, "상품을 찾을 수 없습니다."); return; }
            res.ok(ProductResponse.from(p));
        });

        // CT-01: 상품 설계
        router.post("/products", (req, res) -> {
            ProductCreateRequest body = req.body(ProductCreateRequest.class);
            Date start = parseDate(body.saleStartDate());
            Date end   = parseDate(body.saleEndDate());
            Target target = Target.valueOf(body.target());

            Product product = Product.design(
                    body.productCode(), body.productName(),
                    body.description(), target, start, end
            );
            if (body.riderCodes() != null) {
                List<ProductRider> riders = body.riderCodes().stream()
                        .map(code -> {
                            Rider r = riderList.findByCode(code);
                            return r != null ? ProductRider.from(r) : null;
                        })
                        .filter(r -> r != null)
                        .collect(Collectors.toList());
                product.setRiders(riders);
            }
            productList.save(product);
            res.created(ProductResponse.from(product));
        });

        // CT-04: 상품인가 신청
        router.put("/products/{id}/approval", (req, res) -> {
            Product p = requireProduct(req.pathVariable("id"));
            p.applyForApproval();
            productList.save(p);
            res.ok(ProductResponse.from(p));
        });

        // CT-05: 요율검증 요청 (FSS 제출 → 상태 SALE_PENDING)
        router.put("/products/{id}/rate-verification", (req, res) -> {
            Product p = requireProduct(req.pathVariable("id"));
            p.applySalePermit();
            productList.save(p);
            res.ok(ProductResponse.from(p));
        });

        // CT-06: 상품판매 확정
        router.put("/products/{id}/sale", (req, res) -> {
            Product p = requireProduct(req.pathVariable("id"));
            p.onsale();
            productList.save(p);
            res.ok(ProductResponse.from(p));
        });

        // CS-03: 예상보험료 산출
        router.post("/products/{id}/estimate", (req, res) -> {
            requireProduct(req.pathVariable("id"));
            PremiumEstimateRequest body = req.body(PremiumEstimateRequest.class);
            CarPurpose purpose = CarPurpose.valueOf(body.carPurpose());
            PremiumCalculation calc = PremiumCalculation.calculate(
                    body.carStandardValue(), purpose, java.util.Collections.emptyList()
            );
            res.ok(new PremiumEstimateResponse(
                    calc.getSubtotal(),
                    calc.getFinalPremium(),
                    "KRW"
            ));
        });
    }

    private Product requireProduct(String id) {
        Product p = productList.findById(id);
        if (p == null) throw new common.exception.infra.ForbiddenException("상품을 찾을 수 없습니다.");
        return p;
    }

    private static Date parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(s); }
        catch (ParseException e) { return null; }
    }
}
