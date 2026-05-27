package dto.response.cl;

import java.util.List;

/**
 * CL-01 사고 상세 조회 결과
 */
public record AccidentDetailResponse(
    String       accidentId,
    String       accidentDate,
    String       accidentDetail,
    List<String> documents,
    String       contractId,
    String       coverageDescription,
    long         coverageLimitAmount,   // 원 단위 (0이면 정보 없음)
    String       regionCode
) {}
