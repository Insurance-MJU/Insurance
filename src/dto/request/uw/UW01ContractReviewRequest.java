package dto.request.uw;

/**
 * UW-01 계약인수 심사 결정 요청
 * - decision: "APPROVE" | "REJECT" | "SUPPLEMENT"
 * - rejectReason: decision = "REJECT" 일 때 필수
 * - supplementDocs: decision = "SUPPLEMENT" 일 때 필수
 */
public record UW01ContractReviewRequest(
    String subscriptionNo,
    String reviewOpinion,
    String decision,
    String rejectReason,    // nullable
    String supplementDocs   // nullable
) {}
