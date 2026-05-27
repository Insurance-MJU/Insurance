package dto.response.cl;

/**
 * CL-01 현장조사역 배당 완료 응답
 */
public record AccidentAssignResponse(
    String claimId,
    String assignedEmpNo
) {}
