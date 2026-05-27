package dto.response.cl;

/**
 * CL-02 손해액 산정 완료 응답
 */
public record DamageAssessmentResponse(
    String accNo,
    int    settlement,          // 최종 합의금 (만원)
    int    deductible,          // 자기부담금 (만원)
    int    finalAmount,         // 실 지급 보상금 (만원) = settlement - deductible
    String opinion,
    String statusLabel          // "지급 대기"
) {}
