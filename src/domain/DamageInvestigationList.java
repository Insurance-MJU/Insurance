package domain;

import domain.common.Money;
import infra.dao.DamageInvestigationDao;
import infra.vo.DamageInvestigationVO;

import java.util.Collections;
import java.util.List;

public class DamageInvestigationList {
    private final DamageInvestigationDao dao;
    private final List<DamageInvestigation> investigations;

    public DamageInvestigationList(DamageInvestigationDao dao) {
        this.dao            = dao;
        this.investigations = Collections.emptyList();
    }

    public DamageInvestigationList(List<DamageInvestigation> investigations) {
        this.dao            = null;
        this.investigations = Collections.unmodifiableList(investigations);
    }

    private static DamageInvestigation toDomain(DamageInvestigationVO vo) {
        if (vo == null) return null;
        DamageInvestigation inv = new DamageInvestigation();
        inv.setInvestigationId(vo.investigationId);
        inv.setAccidentId(vo.accidentId);
        inv.setClaimId(vo.claimId);
        inv.setInvestigatorName(vo.investigatorName);
        inv.setOpinion(vo.opinion);
        inv.setDamageCode(vo.damageCode);
        if (vo.injuryGrade > 0) inv.setInjuryGrade(InjuryGrade.fromGrade(vo.injuryGrade));
        inv.setOurFault(vo.ourFault);
        inv.setOtherFault(vo.otherFault);
        inv.setLiability(vo.liability);
        inv.setExpectedRepairCost(new Money(vo.expectedRepairCost, "KRW"));
        inv.setCompensationLimit(new Money(vo.compensationLimit, "KRW"));
        inv.setFinalOpinion(vo.finalOpinion);
        inv.setSavedAt(vo.savedAt);
        return inv;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public DamageInvestigation findByAccidentId(String accidentId) {
        return toDomain(dao.findByAccidentId(accidentId));
    }

    public void save(DamageInvestigation inv) {
        dao.save(new DamageInvestigationVO(
            inv.getInvestigationId(), inv.getAccidentId(), inv.getClaimId(),
            inv.getInvestigatorName(), inv.getOpinion(), inv.getDamageCode(),
            inv.getInjuryGrade() != null ? inv.getInjuryGrade().getGrade() : 0,
            inv.getOurFault(), inv.getOtherFault(), inv.getLiability(),
            inv.getExpectedRepairCost() != null ? inv.getExpectedRepairCost().getAmount() : 0L,
            inv.getCompensationLimit()  != null ? inv.getCompensationLimit().getAmount()  : 0L,
            inv.getFinalOpinion(), inv.getSavedAt()
        ));
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<DamageInvestigation> getAll() { return investigations; }
    public boolean isEmpty() { return investigations.isEmpty(); }
    public int size() { return investigations.size(); }
}
