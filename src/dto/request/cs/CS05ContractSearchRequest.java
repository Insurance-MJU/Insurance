package dto.request.cs;

/**
 * CS-05 보험계약 조회 요청
 * - periodCode: "ALL" | "1Y" | "3Y"
 * - statusCode: "ACTIVE" | "EXPIRED" | "CANCELLED"
 */
public record CS05ContractSearchRequest(
    String holderName,
    String periodCode,
    String statusCode
) {}
