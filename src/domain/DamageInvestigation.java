package domain;

import java.util.Date;

public class DamageInvestigation {
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
    public String getSavedAt() { return savedAt; }

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
