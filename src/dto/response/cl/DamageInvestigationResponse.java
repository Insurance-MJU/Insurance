package dto.response.cl;

/**
 * CL-03 손해 조사 완료 응답
 */
public record DamageInvestigationResponse(
    String accNo,
    String opinion,
    String damageCode,
    String injuryGradeLabel,    // "12급" 등
    int    ourFault,
    int    otherFault,
    String liability,           // "면책" | "부책"
    String finalOpinion,
    String savedAt              // 저장 일시
) {}
