package domain;

import domain.common.Money;
import java.io.Serializable;

public class DamageAssessment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Money settlement;
    private Money deductibleAmount;
    private Money compensationAmount;
    private ClaimPayment claimPayment;

    public DamageAssessment() {}

    public DamageAssessment(Money settlement, Money deductibleAmount, Money compensationAmount) {
        this.settlement = settlement;
        this.deductibleAmount = deductibleAmount;
        this.compensationAmount = compensationAmount;
    }

    public void completePayment(String bank, String accountNo) {
        this.claimPayment = new ClaimPayment(bank, accountNo);
    }

    public Money getSettlement()         { return settlement; }
    public Money getDeductibleAmount()   { return deductibleAmount; }
    public Money getCompensationAmount() { return compensationAmount; }
    public ClaimPayment getClaimPayment() { return claimPayment; }

    public void setSettlement(Money v)         { this.settlement = v; }
    public void setDeductibleAmount(Money v)   { this.deductibleAmount = v; }
    public void setCompensationAmount(Money v) { this.compensationAmount = v; }
    public void setClaimPayment(ClaimPayment v) { this.claimPayment = v; }
}
