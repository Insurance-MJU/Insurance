package infra.vo;

import java.util.Date;

public class RiskAnalysisReportVO {
    public final String subscriptionNo;
    public final double riskScore;
    public final int riskGrade;
    public final double accidentScore;
    public final double drivingExpScore;
    public final double creditGradeScore;
    public final double trafficViolationScore;
    public final double surchargeRate;
    public final long basePremium;
    public final long surchargeAmount;
    public final long totalPremium;
    public final String reviewGuide;
    public final String reviewerName;
    public final Date reviewDate;
    public final String reviewOpinion;

    public RiskAnalysisReportVO(String subscriptionNo, double riskScore, int riskGrade,
                                double accidentScore, double drivingExpScore, double creditGradeScore,
                                double trafficViolationScore, double surchargeRate,
                                long basePremium, long surchargeAmount, long totalPremium,
                                String reviewGuide, String reviewerName, Date reviewDate,
                                String reviewOpinion) {
        this.subscriptionNo        = subscriptionNo;
        this.riskScore             = riskScore;
        this.riskGrade             = riskGrade;
        this.accidentScore         = accidentScore;
        this.drivingExpScore       = drivingExpScore;
        this.creditGradeScore      = creditGradeScore;
        this.trafficViolationScore = trafficViolationScore;
        this.surchargeRate         = surchargeRate;
        this.basePremium           = basePremium;
        this.surchargeAmount       = surchargeAmount;
        this.totalPremium          = totalPremium;
        this.reviewGuide           = reviewGuide;
        this.reviewerName          = reviewerName;
        this.reviewDate            = reviewDate;
        this.reviewOpinion         = reviewOpinion;
    }
}
