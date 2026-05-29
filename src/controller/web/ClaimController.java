package controller.web;

import controller.web.dto.AssessRequest;
import controller.web.dto.ClaimResponse;
import controller.web.dto.PayRequest;
import domain.*;
import domain.common.Money;
import infra.web.Router;

import java.util.List;
import java.util.stream.Collectors;

public class ClaimController {

    private final ClaimList claimList;

    public ClaimController(ClaimList claimList) {
        this.claimList = claimList;
    }

    public void registerRoutes(Router router) {
        router.get("/claims",            (req, res) -> res.ok(getAll()));
        router.get("/claims/{id}",       (req, res) -> res.ok(ClaimResponse.from(claimList.getById(req.pathVariable("id")))));
        router.put("/claims/{id}/assess",(req, res) -> res.ok(assess(req.pathVariable("id"), req.body(AssessRequest.class))));
        router.put("/claims/{id}/pay",   (req, res) -> res.ok(pay(req.pathVariable("id"), req.body(PayRequest.class))));
    }

    private List<ClaimResponse> getAll() {
        return claimList.findAll().getAll().stream()
                .map(ClaimResponse::from)
                .collect(Collectors.toList());
    }

    private ClaimResponse assess(String id, AssessRequest req) {
        Claim c = claimList.getById(id);
        c.assess(new Money(req.settlement(), "KRW"), new Money(req.deductible(), "KRW"));
        claimList.save(c);
        return ClaimResponse.from(c);
    }

    private ClaimResponse pay(String id, PayRequest req) {
        Claim c = claimList.getById(id);
        c.completePayment(req.bank(), req.accountNo());
        claimList.save(c);
        return ClaimResponse.from(c);
    }

}
