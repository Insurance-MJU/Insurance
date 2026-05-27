package dto.request.cl;

/**
 * CL-04 보험금 지급 요청
 */
public record CL04InsurancePaymentRequest(
    String accNo,
    String bank,
    String accountNo,
    String opinionFileName,  // 결재용 사정의견서 파일명
    String authPassword      // 결제 인증 비밀번호
) {}
