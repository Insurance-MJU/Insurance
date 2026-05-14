package domain.contract;

import domain.common.Money;
import infra.util.FileStore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RiskAnalysisReport implements Serializable {
    private static final long serialVersionUID = 1L;

    // ── 정적 저장소 ───────────────────────────────────────────
    private static final List<RiskAnalysisReport> STORE;

    static {
        List<RiskAnalysisReport> loaded = FileStore.load("risk_analysis.dat");
        STORE = (loaded != null) ? loaded : new ArrayList<>();
    }

    public static RiskAnalysisReport findBySubscriptionNo(String subscriptionNo) {
        return STORE.stream()
            .filter(r -> r.getSubscriptionNo().equals(subscriptionNo))
            .findFirst().orElse(null);
    }

    public static void save(RiskAnalysisReport report) {
        STORE.removeIf(r -> r.getSubscriptionNo().equals(report.getSubscriptionNo()));
        STORE.add(report);
        FileStore.save("risk_analysis.dat", STORE);
    }

    private String subscriptionNo;
    private double riskScore;
    private int riskGrade;
    private double accidentScore;
    private double drivingExpScore;
    private double creditGradeScore;
    private double trafficViolationScore;
    private double surchargeRate;
    private Money basePremium;
    private Money surchargeAmount;
    private Money totalPremium;
    private String reviewGuide;
    private String reviewerName;
    private String reviewDate;
    private String reviewOpinion;

    // ── 정적 팩토리: 신용정보 기반 위험 분석 ─────────────────
    public static RiskAnalysisReport analyze(String subscriptionNo, Money basePremium, CreditInfo creditInfo) {
        double accidentScore    = creditInfo.getAccidentCount() * 1.2;
        double drivingExpScore  = creditInfo.getDrivingExperienceYears() <= 2 ? 0.3 : 0.0;
        double creditGradeScore = resolveCreditGradeScore(creditInfo.getCreditGrade());
        double totalScore       = accidentScore + drivingExpScore + creditGradeScore;

        int grade          = resolveGrade(totalScore);
        double surchargeRate = resolveSurchargeRate(grade);

        long base      = basePremium.getAmount();
        long surcharge = Math.round(base * surchargeRate);

        RiskAnalysisReport r = new RiskAnalysisReport();
        r.subscriptionNo       = subscriptionNo;
        r.riskScore            = totalScore;
        r.riskGrade            = grade;
        r.accidentScore        = accidentScore;
        r.drivingExpScore      = drivingExpScore;
        r.creditGradeScore     = creditGradeScore;
        r.trafficViolationScore = 0.0;
        r.surchargeRate        = surchargeRate;
        r.basePremium          = basePremium;
        r.surchargeAmount      = new Money(surcharge, "KRW");
        r.totalPremium         = new Money(base + surcharge, "KRW");
        r.reviewGuide          = grade <= 3 ? "사고이력 존재하나 할증 범위 내" : "고위험 인수 재검토 필요";
        return r;
    }

    // ── 정적 팩토리: 신규 가입자 기본 등급 적용 (A1 흐름) ────
    public static RiskAnalysisReport defaultForNewApplicant(String subscriptionNo, Money basePremium) {
        double surchargeRate = 0.05;
        long base      = basePremium.getAmount();
        long surcharge = Math.round(base * surchargeRate);

        RiskAnalysisReport r = new RiskAnalysisReport();
        r.subscriptionNo    = subscriptionNo;
        r.riskScore         = 0.0;
        r.riskGrade         = 3;
        r.surchargeRate     = surchargeRate;
        r.basePremium       = basePremium;
        r.surchargeAmount   = new Money(surcharge, "KRW");
        r.totalPremium      = new Money(base + surcharge, "KRW");
        r.reviewGuide       = "신규 가입자 기본 위험등급 적용";
        return r;
    }

    // ── 비즈니스 메서드: 심사역 확정 ─────────────────────────
    public void confirm(String reviewerName, String opinion) {
        this.reviewerName  = reviewerName;
        this.reviewOpinion = opinion;
        this.reviewDate    = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public boolean isPassed() {
        return riskGrade <= 3;
    }

    public String getRiskGradeLabel() {
        switch (riskGrade) {
            case 1: return "1등급 [안전]";
            case 2: return "2등급 [보통]";
            case 3: return "3등급 [조금 위험]";
            case 4: return "4등급 [위험]";
            case 5: return "5등급 [고위험]";
            default: return riskGrade + "등급";
        }
    }

    // ── 등급 판정 로직 (도메인 규칙) ─────────────────────────
    private static int resolveGrade(double score) {
        if (score <= 0.5) return 1;
        if (score <= 1.0) return 2;
        if (score <= 2.0) return 3;
        if (score <= 3.0) return 4;
        return 5;
    }

    private static double resolveSurchargeRate(int grade) {
        switch (grade) {
            case 3: return 0.05;
            case 4: return 0.10;
            case 5: return 0.20;
            default: return 0.0;
        }
    }

    private static double resolveCreditGradeScore(String creditGrade) {
        if (creditGrade == null) return 0.0;
        if (creditGrade.contains("5등급") || creditGrade.contains("6등급")) return 0.1;
        if (creditGrade.contains("7등급") || creditGrade.contains("8등급")
                || creditGrade.contains("9등급") || creditGrade.contains("10등급")) return 0.2;
        if (creditGrade.contains("4등급")) return 0.05;
        return 0.0;
    }

    // ── Getters ───────────────────────────────────────────────
    public String getSubscriptionNo()        { return subscriptionNo; }
    public double getRiskScore()             { return riskScore; }
    public int getRiskGrade()                { return riskGrade; }
    public double getAccidentScore()         { return accidentScore; }
    public double getDrivingExpScore()       { return drivingExpScore; }
    public double getCreditGradeScore()      { return creditGradeScore; }
    public double getTrafficViolationScore() { return trafficViolationScore; }
    public double getSurchargeRate()         { return surchargeRate; }
    public Money getBasePremium()            { return basePremium; }
    public Money getSurchargeAmount()        { return surchargeAmount; }
    public Money getTotalPremium()           { return totalPremium; }
    public String getReviewGuide()           { return reviewGuide; }
    public String getReviewerName()          { return reviewerName; }
    public String getReviewDate()            { return reviewDate; }
    public String getReviewOpinion()         { return reviewOpinion; }
}
