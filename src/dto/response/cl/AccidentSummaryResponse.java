package dto.response.cl;

/**
 * CL-01 사고 목록 조회 결과 (한 건)
 */
public record AccidentSummaryResponse(
    String accidentId,
    String accidentDate,
    String reportedBy,      // 고객명
    String description,     // 청구 사유
    String statusLabel
) {}
