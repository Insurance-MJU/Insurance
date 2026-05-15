package domain;

import domain.common.Money;
import java.io.Serializable;

public class Deductible implements Serializable {
    private static final long serialVersionUID = 1L;
    private Money amount;
    private Double rate;
    private DeductibleType type;

    public enum DeductibleType {
        NONE,
        FIXED,
        RATE
    }

    private Deductible() {}

    public static Deductible fixedAmount(Money amount) {
        Deductible d = new Deductible();
        d.amount = amount;
        d.type = DeductibleType.FIXED;
        return d;
    }

    public static Deductible none() {
        Deductible d = new Deductible();
        d.type = DeductibleType.NONE;
        return d;
    }

    public static Deductible rate(Double rate) {
        Deductible d = new Deductible();
        d.rate = rate;
        d.type = DeductibleType.RATE;
        return d;
    }

    public Money getAmount() { return amount; }
    public Double getRate() { return rate; }
    public DeductibleType getType() { return type; }
}
