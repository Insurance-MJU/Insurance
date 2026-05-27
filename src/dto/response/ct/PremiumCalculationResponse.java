package dto.response.ct;

import java.util.List;

/**
 * CT-02 보험료 산출 결과
 */
public record PremiumCalculationResponse(
    long             finalPremium,
    long             reserve,
    List<BreakdownItem> breakdown
) {
    /** 보험료 구성 항목 */
    public record BreakdownItem(
        String label,
        long   amount
    ) {}
}
