package infra.vo;

import java.util.Date;

public class ClaimVO {
    public final String claimId;
    public final String claimantName;
    public final Date claimDate;
    public final String contractId;
    public final String description;
    public final String claimStatus;
    public final String assignedEmployee;
    public final String accidentId;
    public final long settlementAmount;
    public final long deductibleAmount;
    public final long compensationAmount;
    public final String bankName;
    public final String accountNumber;

    public ClaimVO(String claimId, String claimantName, Date claimDate, String contractId,
                   String description, String claimStatus, String assignedEmployee, String accidentId,
                   long settlementAmount, long deductibleAmount, long compensationAmount,
                   String bankName, String accountNumber) {
        this.claimId            = claimId;
        this.claimantName       = claimantName;
        this.claimDate          = claimDate;
        this.contractId         = contractId;
        this.description        = description;
        this.claimStatus        = claimStatus;
        this.assignedEmployee   = assignedEmployee;
        this.accidentId         = accidentId;
        this.settlementAmount   = settlementAmount;
        this.deductibleAmount   = deductibleAmount;
        this.compensationAmount = compensationAmount;
        this.bankName           = bankName;
        this.accountNumber      = accountNumber;
    }
}
