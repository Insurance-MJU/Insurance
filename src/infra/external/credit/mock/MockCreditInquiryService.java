package infra.external.credit.mock;

import infra.external.credit.CreditInquiryService;
import infra.external.credit.dto.CreditInquiryRequest;
import infra.external.credit.dto.CreditInquiryResponse;

import java.util.List;

public class MockCreditInquiryService implements CreditInquiryService {

    @Override
    public CreditInquiryResponse inquire(CreditInquiryRequest request) {
        // SSN 끝자리 기반 결정론적 등급 생성
        String digits = request.ssn().replaceAll("[^0-9]", "");
        int seed = digits.isEmpty() ? 3 : Integer.parseInt(String.valueOf(digits.charAt(digits.length() - 1)));
        int grade = (seed % 5) + 1;
        int expYears = (seed % 10) + 1;

        List<CreditInquiryResponse.AccidentRecord> history = grade <= 2
                ? List.of(new CreditInquiryResponse.AccidentRecord("2024-06-01", "타차가해, 대인처리 1건", 3_000_000L))
                : List.of();

        return new CreditInquiryResponse(
                "홍길동",
                "NICE " + grade + "등급",
                expYears,
                "해당 없음",
                history
        );
    }
}
