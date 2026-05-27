package dto.request.ct;

import java.util.List;

/**
 * CT-01 상품 설계 요청
 * - lobCode: "PERSONAL" | "BUSINESS" | "COMMERCIAL"
 * - saleStart / saleEnd: "yyyy-MM-dd"
 */
public record CT01ProductDesignRequest(
    String productName,
    String productCode,
    String lobCode,
    String saleStart,
    String saleEnd,
    String description,

    // 담보 선택 (담보 ID + 각 담보의 옵션 ID 목록)
    List<CT01CoverageSelection> coverageSelections,

    // 특약 ID 목록
    List<String> riderIds
) {}
