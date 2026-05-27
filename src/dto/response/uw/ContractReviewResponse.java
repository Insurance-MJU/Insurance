package dto.response.uw;

/**
 * UW-01 계약인수 심사 결과
 * - decision: "APPROVE" | "REJECT" | "SUPPLEMENT"
 * - policyNo: APPROVE 시에만 값이 있음
 */
public record ContractReviewResponse(
    String subscriptionNo,
    String decision,
    String policyNo,            // 인수 승인 시 발급된 계약번호
    long   finalPremium,        // 인수 승인 시
    String rejectReason,        // 인수 거절 시
    String supplementDocuments  // 서류보완 요청 시
) {}
