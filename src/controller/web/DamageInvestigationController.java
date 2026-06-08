package controller.web;

import domain.*;
import domain.common.Money;
import infra.web.Router;

import java.util.Map;

public class DamageInvestigationController {

    private final AccidentList accidentList;
    private final ClaimList claimList;
    private final DamageInvestigationList damageInvList;

    public DamageInvestigationController(AccidentList accidentList,
                                         ClaimList claimList,
                                         DamageInvestigationList damageInvList) {
        this.accidentList  = accidentList;
        this.claimList     = claimList;
        this.damageInvList = damageInvList;
    }

    public void registerRoutes(Router router) {
        router.get("/accidents/{id}/investigation",
                (req, res) -> res.ok(getInvestigation(req.pathVariable("id"))));
        router.post("/accidents/{id}/investigation",
                (req, res) -> res.created(investigate(
                        req.pathVariable("id"),
                        req.body(InvestigationRequest.class))));
    }

    private Map<String, Object> getInvestigation(String accidentId) {
        DamageInvestigation inv = damageInvList.findByAccidentId(accidentId);
        if (inv == null) return Map.of("exists", false);
        return Map.of(
                "exists",      true,
                "damageCode",  inv.getDamageCode() != null ? inv.getDamageCode() : "",
                "liability",   inv.getLiability() != null ? inv.getLiability() : "",
                "savedAt",     inv.getSavedAtDisplay() != null ? inv.getSavedAtDisplay() : ""
        );
    }

    private Map<String, Object> investigate(String accidentId, InvestigationRequest req) {
        Accident accident = accidentList.findById(accidentId);

        InjuryGrade injuryGrade = InjuryGrade.fromGrade(req.injuryGrade());

        Money expectedRepairCost = (accident != null && accident.getExpectedRepairCost() != null)
                ? accident.getExpectedRepairCost() : new Money(0, "KRW");
        Money coverageLimit = (accident != null && accident.getCoverageLimit() != null)
                ? accident.getCoverageLimit() : new Money(10_000_000, "KRW");

        DamageInvestigation inv = DamageInvestigation.create(
                accidentId, req.opinion(), req.damageCode(), injuryGrade,
                req.ourFault(), req.otherFault(), req.liability(),
                expectedRepairCost, coverageLimit, req.finalOpinion()
        );

        Claim linkedClaim = claimList.findByAccidentId(accidentId);
        if (linkedClaim != null) inv.setClaimId(linkedClaim.getClaimId());
        damageInvList.save(inv);

        if (accident != null) {
            accident.setStatus(AccidentStatus.IN_PROGRESS);
            accidentList.save(accident);
        }

        return Map.of(
                "accidentId",   accidentId,
                "damageCode",   req.damageCode(),
                "injuryGrade",  injuryGrade.getLabel(),
                "ourFault",     req.ourFault(),
                "otherFault",   req.otherFault(),
                "liability",    req.liability(),
                "savedAt",      inv.getSavedAtDisplay()
        );
    }

    public record InvestigationRequest(
            String opinion,
            String damageCode,
            int    injuryGrade,
            int    ourFault,
            int    otherFault,
            String liability,
            String finalOpinion
    ) {}
}
