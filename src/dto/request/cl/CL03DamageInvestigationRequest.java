package dto.request.cl;

/**
 * CL-03 손해 조사 요청
 * - injuryGrade: 1~14 정수
 * - ourFault / otherFault: 합이 100이 되어야 함
 * - liability: "면책" | "부책"
 */
public record CL03DamageInvestigationRequest(
    String accNo,
    String opinion,
    String damageCode,
    int    injuryGrade,
    int    ourFault,
    int    otherFault,
    String liability,
    String finalOpinion
) {}
