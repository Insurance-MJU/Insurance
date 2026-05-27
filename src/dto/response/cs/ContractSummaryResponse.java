package dto.response.cs;

/**
 * CS-05 계약 목록 조회 결과 (한 건)
 */
public record ContractSummaryResponse(
    String policyNo,
    String productName,
    String statusLabel
) {}
