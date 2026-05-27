package dto.request.cl;

/**
 * CL-02 손해액 산정 요청
 * - settlement / deductible: 만원 단위 정수
 */
public record CL02DamageAssessmentRequest(
    String empNo,
    String accNo,
    int    settlement,    // 최종 합의금 (만원)
    int    deductible,    // 자기부담금 (만원)
    String opinion        // 담당자 결제 의견
) {}
