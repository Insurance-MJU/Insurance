package dto.request.ct;

import java.util.List;

/**
 * CT-02 보험료 산출 요청
 * - saleEnd: "yyyy-MM-dd"
 */
public record CT02PremiumCalculationRequest(
    String       productName,
    List<String> coverageNames,
    String       saleEnd
) {}
