package domain;

import domain.common.Money;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DamageInvestigation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accidentCause;
    private String damageDetail;
    private Date investigationDate;
    private String investigationId;
    private String investigationResult;
    private String investigatorName;
    private double liabilityRatio;

    private String accidentId;
    private DamageAssessment assessment;
    private String opinion;
    private String damageCode;
    private InjuryGrade injuryGrade;
    private int ourFault;
    private int otherFault;
    private String liability;
    private Money expectedRepairCost;
    private Money compensationLimit;
    private String finalOpinion;
    private Date savedAt;

    public DamageInvestigation() {}

    public String getInvestigationResult() { return investigationResult; }
    public boolean investigateDamage() { return false; }
    public boolean updateLiabilityRatio(double ratio) { this.liabilityRatio = ratio; return true; }
    public boolean updateAccidentDetail(String detail) { this.damageDetail = detail; return true; }

    /** 당사+타사 과실 비율 합계가 100%인지 검증 */
    public static boolean validateFaultRatio(int ourFault, int otherFault) {
        return ourFault + otherFault == 100;
    }

    /** 손해조사 결과를 한 번에 생성하는 팩토리 메서드 */
    public static DamageInvestigation create(String accidentId, String opinion, String damageCode,
            InjuryGrade injuryGrade, int ourFault, int otherFault, String liability,
            Money expectedRepairCost, Money compensationLimit, String finalOpinion) {
        DamageInvestigation inv = new DamageInvestigation();
        inv.accidentId = accidentId;
        inv.opinion = opinion;
        inv.damageCode = damageCode;
        inv.injuryGrade = injuryGrade;
        inv.ourFault = ourFault;
        inv.otherFault = otherFault;
        inv.liability = liability;
        inv.expectedRepairCost = expectedRepairCost;
        inv.compensationLimit = compensationLimit;
        inv.finalOpinion = finalOpinion;
        inv.savedAt = new Date();
        return inv;
    }

    public Date getSavedAt() { return savedAt; }
    public String getSavedAtDisplay() { return savedAt != null ? new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(savedAt) : ""; }

    public String getAccidentCause() { return accidentCause; }
    public String getDamageDetail() { return damageDetail; }
    public Date getInvestigationDate() { return investigationDate; }
    public String getInvestigationId() { return investigationId; }
    public String getInvestigatorName() { return investigatorName; }
    public double getLiabilityRatio() { return liabilityRatio; }
    public String getAccidentId()           { return accidentId; }
    public DamageAssessment getAssessment() { return assessment; }
    public String getOpinion()              { return opinion; }
    public String getDamageCode() { return damageCode; }
    public InjuryGrade getInjuryGrade() { return injuryGrade; }
    public int getOurFault() { return ourFault; }
    public int getOtherFault() { return otherFault; }
    public String getLiability() { return liability; }
    public Money getExpectedRepairCost() { return expectedRepairCost; }
    public Money getCompensationLimit() { return compensationLimit; }
    public String getFinalOpinion() { return finalOpinion; }

    // ── DAO 위임 ──────────────────────────────────────────────
    public void save()                                                     { infra.dao.DamageInvestigationDao.getInstance().save(this); }
    public static DamageInvestigation findByAccidentId(String accidentId)  { return infra.dao.DamageInvestigationDao.getInstance().findByAccidentId(accidentId); }

    public void setAccidentCause(String v) { this.accidentCause = v; }
    public void setDamageDetail(String v) { this.damageDetail = v; }
    public void setInvestigationId(String v) { this.investigationId = v; }
    public void setInvestigationResult(String v) { this.investigationResult = v; }
    public void setInvestigatorName(String v) { this.investigatorName = v; }
    public void setAccidentId(String v)        { this.accidentId = v; }
    public void setAssessment(DamageAssessment v) { this.assessment = v; }
    public void setOpinion(String v)           { this.opinion = v; }
    public void setDamageCode(String v) { this.damageCode = v; }
    public void setInjuryGrade(InjuryGrade v) { this.injuryGrade = v; }
    public void setOurFault(int v) { this.ourFault = v; }
    public void setOtherFault(int v) { this.otherFault = v; }
    public void setLiability(String v) { this.liability = v; }
    public void setExpectedRepairCost(Money v) { this.expectedRepairCost = v; }
    public void setCompensationLimit(Money v) { this.compensationLimit = v; }
    public void setFinalOpinion(String v) { this.finalOpinion = v; }
    public void setSavedAt(Date v) { this.savedAt = v; }
}
