package controller.web;

import controller.web.dto.RejectRequest;
import controller.web.dto.SubscriptionCreateRequest;
import controller.web.dto.SubscriptionResponse;
import common.util.DateUtil;
import domain.*;
import domain.common.Money;
import infra.external.verification.VerificationService;
import infra.external.verification.dto.VerifiedIdentity;
import infra.web.Router;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionController {

    private final SubscriptionList subscriptionList;
    private final ProductList productList;
    private final VerificationService verificationService;

    public SubscriptionController(SubscriptionList subscriptionList, ProductList productList,
                                  VerificationService verificationService) {
        this.subscriptionList = subscriptionList;
        this.productList = productList;
        this.verificationService = verificationService;
    }

    public void registerRoutes(Router router) {
        router.get("/subscriptions",                 (req, res) -> res.ok(getAll()));
        router.get("/subscriptions/pending",         (req, res) -> res.ok(getPending()));
        router.get("/subscriptions/{no}",            (req, res) -> res.ok(SubscriptionResponse.from(subscriptionList.getByNo(req.pathVariable("no")))));
        router.post("/subscriptions",                (req, res) -> res.created(create(req.body(SubscriptionCreateRequest.class))));
        router.put("/subscriptions/{no}/approve",    (req, res) -> res.ok(approve(req.pathVariable("no"))));
        router.put("/subscriptions/{no}/reject",     (req, res) -> res.ok(reject(req.pathVariable("no"), req.body(RejectRequest.class).reason())));
        router.put("/subscriptions/{no}/supplement", (req, res) -> res.ok(supplement(req.pathVariable("no"), req.body(RejectRequest.class).reason())));
    }

    private List<SubscriptionResponse> getAll() {
        return subscriptionList.findAll().getAll().stream()
                .map(SubscriptionResponse::from)
                .collect(Collectors.toList());
    }

    private List<SubscriptionResponse> getPending() {
        return subscriptionList.findPendingReview().getAll().stream()
                .map(SubscriptionResponse::from)
                .collect(Collectors.toList());
    }

    private SubscriptionResponse create(SubscriptionCreateRequest req) {
        VerifiedIdentity identity = verificationService.resolveIdentity(req.verificationToken());

        Product product = productList.getById(req.productId());
        product.validateOnSale();

        int age = Party.calcAge(identity.ssn());

        Subscription subscription = Subscription.register(
                subscriptionList.nextSubscriptionNo(),
                identity.name(), identity.ssn(), req.address(),
                req.carNumber(), req.chassisNumber(),
                product.getProductName(),
                new Money(req.premium(), "KRW"),
                new Money(req.premium(), "KRW"),
                DateUtil.format(new Date()),
                req.occupation(), age,
                product.getDefaultCoverageDescription()
        );
        subscriptionList.save(subscription);
        return SubscriptionResponse.from(subscription);
    }

    private SubscriptionResponse approve(String no) {
        Subscription s = subscriptionList.getByNo(no);
        s.approve();
        subscriptionList.save(s);
        return SubscriptionResponse.from(s);
    }

    private SubscriptionResponse reject(String no, String reason) {
        Subscription s = subscriptionList.getByNo(no);
        s.reject(reason);
        subscriptionList.save(s);
        return SubscriptionResponse.from(s);
    }

    private SubscriptionResponse supplement(String no, String reason) {
        Subscription s = subscriptionList.getByNo(no);
        s.requestSupplement(reason);
        subscriptionList.save(s);
        return SubscriptionResponse.from(s);
    }
}
