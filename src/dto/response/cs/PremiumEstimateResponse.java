package dto.response.cs;

import java.util.List;

/**
 * CS-03 예상 보험료 산출 결과
 */
public record PremiumEstimateResponse(
    String productName,
    long   basePremium,
    List<DiscountItem> discounts,
    long   finalPremium
) {
    /** 할인 항목 (특약 할인, 안전장치 할인 등) */
    public record DiscountItem(
        String label,
        long   amount
    ) {}
}
