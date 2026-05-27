package dto.response.cs;

/**
 * CS-04 보험금 청구 완료 응답
 */
public record ClaimSubmitResponse(
    String accidentId,
    String accidentDate,
    String accidentLocation,
    String accidentDetail,
    String statusLabel,
    String guide    // "담당자가 배정되면 연락드리겠습니다. (1~3 영업일 소요)"
) {}
