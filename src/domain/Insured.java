package domain;

import java.io.Serializable;
import domain.common.Money;

public class Insured implements Serializable {
    private static final long serialVersionUID = 1L;
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
