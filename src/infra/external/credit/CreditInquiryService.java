package infra.external.credit;

import infra.external.credit.dto.CreditInquiryRequest;
import infra.external.credit.dto.CreditInquiryResponse;

/**
 * 신용정보 조회 외부 서비스 인터페이스
 * 실제 구현체는 신용정보원(NICE/KCB) API와 연동
 */
public interface CreditInquiryService {
    CreditInquiryResponse inquire(CreditInquiryRequest request);
}
