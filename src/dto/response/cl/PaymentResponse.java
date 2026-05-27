package dto.response.cl;

/**
 * CL-04 보험금 지급 완료 응답
 */
public record PaymentResponse(
    String accNo,
    String claimantName,
    long   paymentAmount,   // 원 단위
    String bank,
    String accountNo,
    String statusLabel      // "종결"
) {}
