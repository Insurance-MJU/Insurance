package dto.response.cs;

/**
 * CS-01 상품가입 완료 응답
 */
public record SubscriptionResponse(
    String subscriptionNo,
    String productName,
    String applicantName,
    String phone,
    String carNumber,
    String purposeLabel,
    String driverScopeLabel,
    long   premium,
    String statusLabel,
    String message          // "청약이 완료되었습니다. 심사 후 계약이 확정됩니다."
) {}
