package infra.external;

import domain.common.Money;
import domain.CreditInfo;

import java.util.Arrays;

// 신용정보원(NICE/KCB) 외부 시스템 Mock
public class CreditBureauClient {

    private static final CreditBureauClient INSTANCE = new CreditBureauClient();
    public static CreditBureauClient getInstance() { return INSTANCE; }

    public boolean isAvailable() {
        return true;
    }

    public CreditInfo findByApplicant(String ssn, String carNumber) {
        if ("020101-3******".equals(ssn) && "64마0866".equals(carNumber)) {
            CreditInfo info = new CreditInfo();
            info.setApplicantName("박수현");
            info.setSsn(ssn);
            info.setCarNumber(carNumber);
            info.setAccidentHistory(Arrays.asList(
                new CreditInfo.AccidentRecord("2025-08-01", "타차가해, 대인처리 1건", new Money(3_000_000L, "KRW"))
            ));
            info.setDrivingExperienceYears(2);
            info.setCreditGrade("NICE 5등급");
            info.setFraudHistory("해당 없음");
            return info;
        }
        return null;
    }
}
