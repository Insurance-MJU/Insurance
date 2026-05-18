package domain;

import domain.common.Money;
import java.io.Serializable;

public class DamageAssessment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Money settlement;
    private Deductible deductible;
    private Money compensationAmount;

    public DamageAssessment() {}

    public DamageAssessment(Money settlement, Deductible deductible, Money compensationAmount) {
        this.settlement = settlement;
        this.deductible = deductible;
        this.compensationAmount = compensationAmount;
    }

    public Money getSettlement() { return settlement; }
    public Deductible getDeductible() { return deductible; }
    public Money getCompensationAmount() { return compensationAmount; }

    public void setSettlement(Money v) { this.settlement = v; }
    public void setDeductible(Deductible v) { this.deductible = v; }
    public void setCompensationAmount(Money v) { this.compensationAmount = v; }
}
