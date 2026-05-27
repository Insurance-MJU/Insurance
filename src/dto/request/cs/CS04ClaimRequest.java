package dto.request.cs;

import java.util.List;

/**
 * CS-04 보험금 청구 요청
 * - 본인인증은 별도 엔드포인트에서 처리 후 name/phone 전달 가정
 * - documents: 업로드된 파일명 목록
 */
public record CS04ClaimRequest(
    // 본인 인증 결과
    String name,
    String phone,

    // 선택된 계약
    String policyNo,

    // 사고 기초 정보
    String accidentDate,    // "yyyy-MM-dd HH:mm"
    String accidentPlace,
    String accidentDetail,

    // 증빙 서류 (파일명 목록)
    List<String> documents,

    // 최종 제출 동의
    boolean agreedToSubmit
) {}
