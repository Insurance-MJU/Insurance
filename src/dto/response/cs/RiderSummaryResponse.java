package dto.response.cs;

/**
 * 특약 요약 (ProductDetailResponse 내부에서 사용)
 */
public record RiderSummaryResponse(
    String riderId,
    String riderName,
    String discountLabel    // 예: "10% 할인" — 없으면 null
) {}
