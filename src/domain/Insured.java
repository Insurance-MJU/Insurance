package domain;

import domain.common.Money;

public class Insured {
    private String insuredId;
    private String insuredName;
    private InsuredType insuredType;
    private Money insuredValue;

    public enum InsuredType {}

    public InsuredType getInsuredType() { return insuredType; }
    public String getInsuredId() { return insuredId; }
    public String getInsuredName() { return insuredName; }
    public Money getInsuredValue() { return insuredValue; }
}
