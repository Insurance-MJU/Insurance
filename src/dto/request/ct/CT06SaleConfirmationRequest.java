package dto.request.ct;

/**
 * CT-06 상품판매 확정 요청
 */
public record CT06SaleConfirmationRequest(
    String productCode,
    String confirmPassword  // 결제 인증
) {}
