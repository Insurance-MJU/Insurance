package infra.vo;

import java.util.Date;

public class DamageInvestigationVO {
    public final String investigationId;
    public final String accidentId;
    public final String claimId;
    public final String investigatorName;
    public final String opinion;
    public final String damageCode;
    public final int injuryGrade;
    public final int ourFault;
    public final int otherFault;
    public final String liability;
    public final long expectedRepairCost;
    public final long compensationLimit;
    public final String finalOpinion;
    public final Date savedAt;

    public DamageInvestigationVO(String investigationId, String accidentId, String claimId,
                                 String investigatorName, String opinion, String damageCode,
                                 int injuryGrade, int ourFault, int otherFault, String liability,
                                 long expectedRepairCost, long compensationLimit,
                                 String finalOpinion, Date savedAt) {
        this.investigationId   = investigationId;
        this.accidentId        = accidentId;
        this.claimId           = claimId;
        this.investigatorName  = investigatorName;
        this.opinion           = opinion;
        this.damageCode        = damageCode;
        this.injuryGrade       = injuryGrade;
        this.ourFault          = ourFault;
        this.otherFault        = otherFault;
        this.liability         = liability;
        this.expectedRepairCost = expectedRepairCost;
        this.compensationLimit = compensationLimit;
        this.finalOpinion      = finalOpinion;
        this.savedAt           = savedAt;
    }
}
