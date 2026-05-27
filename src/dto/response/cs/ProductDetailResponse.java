package dto.response.cs;

import java.util.List;

/**
 * CS-02 상품 상세 조회 결과
 */
public record ProductDetailResponse(
    String productCode,
    String productName,
    String targetLabel,
    String saleStart,
    String saleEnd,
    String description,
    String statusLabel,
    List<CoverageSummaryResponse> coverages,
    List<RiderSummaryResponse>    riders
) {}
