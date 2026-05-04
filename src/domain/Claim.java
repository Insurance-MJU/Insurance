package domain;

public class Claim {
    private String claimId;
    private String accidentId;
    private String claimantName;
    private String claimDate;
    private String contractId;
    private String description;
    private String status;
    private String assignedEmployee;
    private int settlement;
    private int deductible;
    private int compensationAmount;
    private String bankName;
    private String accountNumber;

    public enum ClaimStatus { PENDING, INVESTIGATING, ASSESSING, PAYMENT_PENDING, CLOSED }
    public enum ClaimType { PROPERTY, PERSONAL }

    public Claim() {}

    public Claim(String claimId, String accidentId, String claimantName, String claimDate,
                 String contractId, String description, String status) {
        this.claimId = claimId;
        this.accidentId = accidentId;
        this.claimantName = claimantName;
        this.claimDate = claimDate;
        this.contractId = contractId;
        this.description = description;
        this.status = status;
    }

    public String createClaim() { return claimId; }
    public String getClaimInfo() { return null; }
    public boolean updateStatus(ClaimStatus s) { return false; }

    public String getClaimId() { return claimId; }
    public String getAccidentId() { return accidentId; }
    public String getClaimantName() { return claimantName; }
    public String getClaimDate() { return claimDate; }
    public String getContractId() { return contractId; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getAssignedEmployee() { return assignedEmployee; }
    public int getSettlement() { return settlement; }
    public int getDeductible() { return deductible; }
    public int getCompensationAmount() { return compensationAmount; }
    public String getBankName() { return bankName; }
    public String getAccountNumber() { return accountNumber; }

    public void setClaimId(String v) { this.claimId = v; }
    public void setAccidentId(String v) { this.accidentId = v; }
    public void setClaimantName(String v) { this.claimantName = v; }
    public void setClaimDate(String v) { this.claimDate = v; }
    public void setContractId(String v) { this.contractId = v; }
    public void setDescription(String v) { this.description = v; }
    public void setStatus(String v) { this.status = v; }
    public void setAssignedEmployee(String v) { this.assignedEmployee = v; }
    public void setSettlement(int v) { this.settlement = v; }
    public void setDeductible(int v) { this.deductible = v; }
    public void setCompensationAmount(int v) { this.compensationAmount = v; }
    public void setBankName(String v) { this.bankName = v; }
    public void setAccountNumber(String v) { this.accountNumber = v; }
}
