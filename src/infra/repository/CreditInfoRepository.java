package infra.repository;

import domain.CreditInfo;
import domain.common.Money;

import java.util.Arrays;
import java.util.Collections;

// 외부 신용정보원 시스템 연동 시뮬레이션 (실 연동 없이 조회 대상별 고정 데이터 반환)
public class CreditInfoRepository {

    public static CreditInfo findByApplicant(String ssn, String carNumber) {
        if ("020101-3******".equals(ssn) && "64마0866".equals(carNumber)) {
            CreditInfo info = new CreditInfo();
            info.setApplicantName("박수현");
            info.setSsn(ssn);
            info.setCarNumber(carNumber);
            info.setAccidentHistory(Arrays.asList(
                new CreditInfo.AccidentRecord(
                    "2025-08-01",
                    "타차가해, 대인처리 1건",
                    new Money(3_000_000L, "KRW")
                )
            ));
            info.setDrivingExperienceYears(2);
            info.setCreditGrade("NICE 5등급");
            info.setFraudHistory("해당 없음");
            return info;
        }
        // 조회 이력 없음 (신규 가입자 등) → A1 처리
        return null;
    }
}
