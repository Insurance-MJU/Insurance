package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DamageInvestigation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accidentCause;
    private String claimId;
    private String damageDetail;
    private Date investigationDate;
    private String investigationId;
    private String investigationResult;
    private String investigatorName;
    private double liabilityRatio;

    private String accidentId;
    private String opinion;
    private String damageCode;
    private int injuryGrade;
    private int ourFault;
    private int otherFault;
    private String liability;
    private String expectedRepairCost;
    private String compensationLimit;
    private String finalOpinion;
    private String savedAt;

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
            int injuryGrade, int ourFault, int otherFault, String liability,
            String expectedRepairCost, String compensationLimit, String finalOpinion) {
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
        inv.savedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH:mm:ss"));
        return inv;
    }

    public String getSavedAt() { return savedAt; }

    public String getAccidentCause() { return accidentCause; }
    public String getClaimId() { return claimId; }
    public String getDamageDetail() { return damageDetail; }
    public Date getInvestigationDate() { return investigationDate; }
    public String getInvestigationId() { return investigationId; }
    public String getInvestigatorName() { return investigatorName; }
    public double getLiabilityRatio() { return liabilityRatio; }
    public String getAccidentId() { return accidentId; }
    public String getOpinion() { return opinion; }
    public String getDamageCode() { return damageCode; }
    public int getInjuryGrade() { return injuryGrade; }
    public int getOurFault() { return ourFault; }
    public int getOtherFault() { return otherFault; }
    public String getLiability() { return liability; }
    public String getExpectedRepairCost() { return expectedRepairCost; }
    public String getCompensationLimit() { return compensationLimit; }
    public String getFinalOpinion() { return finalOpinion; }

    public void setAccidentCause(String v) { this.accidentCause = v; }
    public void setClaimId(String v) { this.claimId = v; }
    public void setDamageDetail(String v) { this.damageDetail = v; }
    public void setInvestigationId(String v) { this.investigationId = v; }
    public void setInvestigationResult(String v) { this.investigationResult = v; }
    public void setInvestigatorName(String v) { this.investigatorName = v; }
    public void setAccidentId(String v) { this.accidentId = v; }
    public void setOpinion(String v) { this.opinion = v; }
    public void setDamageCode(String v) { this.damageCode = v; }
    public void setInjuryGrade(int v) { this.injuryGrade = v; }
    public void setOurFault(int v) { this.ourFault = v; }
    public void setOtherFault(int v) { this.otherFault = v; }
    public void setLiability(String v) { this.liability = v; }
    public void setExpectedRepairCost(String v) { this.expectedRepairCost = v; }
    public void setCompensationLimit(String v) { this.compensationLimit = v; }
    public void setFinalOpinion(String v) { this.finalOpinion = v; }
    public void setSavedAt(String v) { this.savedAt = v; }
}
