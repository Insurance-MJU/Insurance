package domain;

import domain.common.Money;

public class Deductible {
    private Money amount;
    private Double rate;
    private DeductibleType type;

    public enum DeductibleType {
        NONE,        // 자기부담금 없음
        FIXED,       // 정액 (예: 20만원 고정)
        RATE         // 비율 (예: 손해액의 20%)
    }

    private Deductible() {}

    public static Deductible fixedAmount(Money amount) {
        Deductible d = new Deductible();
        d.amount = amount;
        return d;
    }

    public static Deductible none() {
        return new Deductible();
    }

    public static Deductible rate(Double rate) {
        Deductible d = new Deductible();
        d.rate = rate;
        return d;
    }

    public Money getAmount() { return amount; }
    public Double getRate() { return rate; }
    public DeductibleType getType() { return type; }
}
