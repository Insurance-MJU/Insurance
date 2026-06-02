package infra.dao;

import domain.CreditInfo;
import domain.common.Money;
import infra.external.credit.CreditInquiryService;
import infra.external.credit.dto.CreditInquiryRequest;
import infra.external.credit.dto.CreditInquiryResponse;

import java.util.stream.Collectors;

public class CreditDao {
    private final CreditInquiryService creditService;

    public CreditDao(CreditInquiryService creditService) {
        this.creditService = creditService;
    }

    public CreditInfo findByApplicant(String ssn, String carNumber) {
        CreditInquiryResponse r = creditService.inquire(new CreditInquiryRequest(ssn, carNumber));
        if (r == null) return null;

        CreditInfo info = new CreditInfo();
        info.setApplicantName(r.applicantName());
        info.setCreditGrade(r.creditGrade());
        info.setDrivingExperienceYears(r.drivingExperienceYears());
        info.setFraudHistory(r.fraudHistory());
        if (r.accidentHistory() != null) {
            info.setAccidentHistory(r.accidentHistory().stream()
                .map(a -> new CreditInfo.AccidentRecord(
                    a.date(), a.description(), new Money(a.amountKrw(), "KRW")))
                .collect(Collectors.toList()));
        }
        return info;
    }
}
