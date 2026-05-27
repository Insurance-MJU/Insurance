package dto.response.cs;

/**
 * CS-02 상품 목록 조회 결과 (한 건)
 */
public record ProductSummaryResponse(
    String productCode,
    String productName,
    String targetLabel,     // "개인용" | "업무용" | "영업용"
    String saleStart,
    String saleEnd,
    String statusLabel      // "판매중" | "판매종료" 등
) {}
