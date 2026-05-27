package dto.response.cl;

/**
 * CL-01 현장조사역 후보 목록 (한 건)
 */
public record InvestigatorSummaryResponse(
    String employeeId,
    String name,
    int    openCaseCount    // 미결 건수
) {}
