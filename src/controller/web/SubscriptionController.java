package controller.web;

import controller.web.dto.SubscriptionCreateRequest;
import controller.web.dto.SubscriptionResponse;
import domain.*;
import domain.common.Money;
import infra.web.Router;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionController {

    private final SubscriptionList subscriptionList;
    private final ProductList productList;

    public SubscriptionController(SubscriptionList subscriptionList, ProductList productList) {
        this.subscriptionList = subscriptionList;
        this.productList = productList;
    }

    public void registerRoutes(Router router) {
        // CS-05: 청약 목록 조회
        router.get("/subscriptions", (req, res) -> {
            List<SubscriptionResponse> result = subscriptionList.findAll().getAll().stream()
                    .map(SubscriptionResponse::from)
                    .collect(Collectors.toList());
            res.ok(result);
        });

        // CS-05: 청약 상세 조회
        router.get("/subscriptions/{no}", (req, res) -> {
            Subscription s = subscriptionList.findByNo(req.pathVariable("no"));
            if (s == null) { res.error(404, "청약을 찾을 수 없습니다."); return; }
            res.ok(SubscriptionResponse.from(s));
        });

        // CS-01: 상품가입 요청
        router.post("/subscriptions", (req, res) -> {
            SubscriptionCreateRequest body = req.body(SubscriptionCreateRequest.class);

            Product product = productList.findById(body.productId());
            if (product == null) { res.error(404, "상품을 찾을 수 없습니다."); return; }
            if (!product.isOnSale()) { res.error(400, "현재 판매 중인 상품이 아닙니다."); return; }

            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Subscription subscription = Subscription.register(
                    subscriptionList.nextSubscriptionNo(),
                    body.applicantName(),
                    body.ssn(),
                    body.address(),
                    body.carNumber(),
                    body.chassisNumber(),
                    product.getProductName(),
                    new Money(body.premium(), "KRW"),
                    new Money(body.premium(), "KRW"),
                    today,
                    body.occupation(),
                    body.age(),
                    product.getDefaultCoverageDescription()
            );
            subscriptionList.save(subscription);
            res.created(SubscriptionResponse.from(subscription));
        });

        // UW-01: 계약인수 심사 대상 청약 목록
        router.get("/subscriptions/pending", (req, res) -> {
            List<SubscriptionResponse> result = subscriptionList.findPendingReview().getAll().stream()
                    .map(SubscriptionResponse::from)
                    .collect(Collectors.toList());
            res.ok(result);
        });

        // UW-01: 청약 심사 결정 (approve/reject/supplement)
        router.put("/subscriptions/{no}/review", (req, res) -> {
            Subscription s = subscriptionList.findByNo(req.pathVariable("no"));
            if (s == null) { res.error(404, "청약을 찾을 수 없습니다."); return; }

            var body = req.body(ReviewRequest.class);
            switch (body.decision().toUpperCase()) {
                case "APPROVE"    -> s.approve();
                case "REJECT"     -> s.reject(body.reason());
                case "SUPPLEMENT" -> s.requestSupplement(body.reason());
                default           -> { res.error(400, "decision은 APPROVE/REJECT/SUPPLEMENT 중 하나여야 합니다."); return; }
            }
            subscriptionList.save(s);
            res.ok(SubscriptionResponse.from(s));
        });
    }

    private record ReviewRequest(String decision, String reason) {}
}
