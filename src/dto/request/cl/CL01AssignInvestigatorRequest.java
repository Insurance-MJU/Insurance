package dto.request.cl;

/**
 * CL-01 현장조사역 배당 확정 요청 (Step 9)
 * - 조회(상세, 담당자 후보)와 배당 확정을 분리
 */
public record CL01AssignInvestigatorRequest(
    String accidentId,
    String customerName,
    String phone,
    String specialty,   // 전문 분야 (예: "자동차 대물")
    String empNo        // 배당할 직원 번호
) {}
