package dto.response.cs;

/**
 * 담보 요약 (ProductDetailResponse 내부에서 사용)
 */
public record CoverageSummaryResponse(
    String coverageName,
    String coverageType,    // "대인배상I(필수)" 등
    boolean mandatory,
    String limitDescription // 한도 옵션 요약
) {}
