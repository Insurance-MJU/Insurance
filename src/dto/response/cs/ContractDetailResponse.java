package dto.response.cs;

/**
 * CS-05 계약 상세 조회 결과
 */
public record ContractDetailResponse(
    String policyNo,
    String productName,
    String issueDate,
    long   premium,
    String coveragesDescription,
    String ridersDescription,
    String carNumber,
    String statusLabel
) {}
