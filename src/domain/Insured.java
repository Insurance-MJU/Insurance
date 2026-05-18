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

    // ── 정적 팩토리: 기명피보험자(운전자) 생성 ────────────────────
    public static Insured ofDriver(String name, String ssn) {
        Insured i = new Insured();
        i.insuredId   = "INS-" + ssn.replace("-", "").substring(0, 6);
        i.insuredName = name;
        i.insuredType = InsuredType.DRIVER;
        return i;
    }

    // Setters
    public void setInsuredId(String v)       { this.insuredId = v; }
    public void setInsuredName(String v)     { this.insuredName = v; }
    public void setInsuredType(InsuredType v){ this.insuredType = v; }
    public void setInsuredValue(Money v)     { this.insuredValue = v; }

    // Getters
    public InsuredType getInsuredType() { return insuredType; }
    public String getInsuredId()        { return insuredId; }
    public String getInsuredName()      { return insuredName; }
    public Money getInsuredValue()      { return insuredValue; }
}
