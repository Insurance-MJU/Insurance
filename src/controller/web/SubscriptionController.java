package controller.web;

import controller.web.dto.RejectRequest;
import controller.web.dto.SubscriptionCreateRequest;
import controller.web.dto.SubscriptionResponse;
import common.util.DateUtil;
import domain.*;
import domain.Contract;
import domain.common.Money;
import infra.external.verification.VerificationService;
import infra.external.verification.dto.VerifiedIdentity;
import infra.web.Router;
import infra.web.auth.JwtUtil;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionController {

    private final SubscriptionList subscriptionList;
    private final ProductList productList;
    private final ContractList contractList;
    private final VerificationService verificationService;
    private final JwtUtil jwtUtil;

    public SubscriptionController(SubscriptionList subscriptionList, ProductList productList,
                                  ContractList contractList, VerificationService verificationService,
                                  JwtUtil jwtUtil) {
        this.subscriptionList = subscriptionList;
        this.productList = productList;
        this.contractList = contractList;
        this.verificationService = verificationService;
        this.jwtUtil = jwtUtil;
    }

    public void registerRoutes(Router router) {
        router.get("/subscriptions",                 (req, res) -> res.ok(getAll(req.header("Authorization"))));
        router.get("/subscriptions/pending",         (req, res) -> res.ok(getPending()));
        router.get("/subscriptions/{no}",            (req, res) -> res.ok(SubscriptionResponse.from(subscriptionList.getByNo(req.pathVariable("no")))));
        router.post("/subscriptions",                (req, res) -> res.created(create(req, req.body(SubscriptionCreateRequest.class))));
        router.put("/subscriptions/{no}/approve",    (req, res) -> res.ok(approve(req.pathVariable("no"))));
        router.put("/subscriptions/{no}/reject",     (req, res) -> res.ok(reject(req.pathVariable("no"), req.body(RejectRequest.class).reason())));
        router.put("/subscriptions/{no}/supplement", (req, res) -> res.ok(supplement(req.pathVariable("no"), req.body(RejectRequest.class).reason())));
    }

    private String extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return jwtUtil.extractUserId(authHeader.substring(7));
    }

    private String extractRole(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return jwtUtil.extractRole(authHeader.substring(7));
    }

    private List<SubscriptionResponse> getAll(String authHeader) {
        String role = extractRole(authHeader);
        String userId = extractUserId(authHeader);
        // 직원/관리자는 전체 조회, 고객은 본인 것만
        SubscriptionList list = ("EMPLOYEE".equals(role) || "ADMIN".equals(role))
                ? subscriptionList.findAll()
                : (userId != null && !userId.isBlank())
                    ? subscriptionList.findByUserId(userId)
                    : subscriptionList.findAll();
        return list.getAll().stream()
                .map(s -> {
                    Contract contract = contractList.findBySubscriptionNo(s.getSubscriptionNo());
                    return contract != null
                            ? SubscriptionResponse.from(s, contract.getContractId())
                            : SubscriptionResponse.from(s);
                })
                .collect(Collectors.toList());
    }

    private List<SubscriptionResponse> getPending() {
        return subscriptionList.findPendingReview().getAll().stream()
                .map(SubscriptionResponse::from)
                .collect(Collectors.toList());
    }

    private SubscriptionResponse create(infra.web.dto.HttpRequest httpReq, SubscriptionCreateRequest req) {
        VerifiedIdentity identity = verificationService.resolveIdentity(req.verificationToken());

        Product product = productList.getById(req.productId());
        product.validateOnSale();

        int age = Party.calcAge(identity.ssn());

        String userId = extractUserId(httpReq.header("Authorization"));
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
        subscription.setUserId(userId);
        subscriptionList.save(subscription);
        return SubscriptionResponse.from(subscription);
    }

    private SubscriptionResponse approve(String no) {
        Subscription s = subscriptionList.getByNo(no);
        s.approve();
        subscriptionList.save(s);

        Party holder = new Party();
        holder.setPartyId("PARTY-" + s.getSubscriptionNo());
        holder.setName(s.getApplicantName());

        String policyNo    = contractList.nextPolicyNo();
        String contractId  = contractList.nextContractId();
        Contract contract  = Contract.issue(
                policyNo, contractId, s.getProductName(),
                holder, s.getPremium(), s.getCarNumber(),
                s.getCoveragesDescription(), "", ""
        );
        contract.setSubscriptionNo(s.getSubscriptionNo());
        contractList.save(contract);

        return SubscriptionResponse.from(s, contractId);
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
