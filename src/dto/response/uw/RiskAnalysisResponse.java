package dto.response.uw;

/**
 * UW-02 위험성 분석 결과
 */
public record RiskAnalysisResponse(
    String subscriptionNo,
    String riskGradeLabel,      // "1등급(안전)" 등
    double riskScore,
    double surchargeRate,       // 예: 0.1 → 10%

    // 점수 세부 내역
    double accidentScore,
    double drivingExpScore,
    double creditGradeScore,

    // 보험료
    long   basePremium,
    long   surchargeAmount,
    long   totalPremium,

    String reviewGuide          // 심사 가이드 메시지
) {}
