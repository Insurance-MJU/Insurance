package dto.response.uw;

/**
 * UW-01 심사 대기 목록 (한 건)
 */
public record SubscriptionSummaryResponse(
    String subscriptionNo,
    String applicantName,
    String productName,
    long   premium,
    String subscriptionDate,
    String statusLabel
) {}
