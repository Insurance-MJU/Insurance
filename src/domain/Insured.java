package domain;

import java.io.Serializable;
import domain.common.Money;

public class Insured implements Serializable {
    private static final long serialVersionUID = 1L;
    private String insuredId;
    private String insuredName;
    private InsuredType insuredType;
    private Money insuredValue;

    public enum InsuredType {
        VEHICLE,    // 피보험차량 (Car)
        DRIVER,     // 기명피보험자 (운전자 본인)
        PASSENGER,  // 탑승자
        THIRD_PARTY // 제3자 (대인·대물 피해자)
    }

    public InsuredType getInsuredType() { return insuredType; }
    public String getInsuredId() { return insuredId; }
    public String getInsuredName() { return insuredName; }
    public Money getInsuredValue() { return insuredValue; }
}
