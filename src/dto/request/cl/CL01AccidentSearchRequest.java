package dto.request.cl;

/**
 * CL-01 사고 접수 목록 조회 요청 (Step 3)
 */
public record CL01AccidentSearchRequest(
    String period,      // "yyyy-MM-dd"
    String status       // "미처리" | "처리중" | "완료"
) {}
