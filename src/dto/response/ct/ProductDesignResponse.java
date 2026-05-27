package dto.response.ct;

import java.util.List;

/**
 * CT-01 상품 설계 완료 응답
 */
public record ProductDesignResponse(
    String       productCode,
    String       productName,
    String       targetLabel,
    String       saleStart,
    String       saleEnd,
    List<String> coverageNames,
    List<String> riderNames,
    long         finalPremium,
    long         reserve,
    String       statusLabel
) {}
