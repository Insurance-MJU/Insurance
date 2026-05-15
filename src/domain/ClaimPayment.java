package domain;

import java.io.Serializable;

public class ClaimPayment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bankName;
    private String accountNumber;

    public ClaimPayment() {}

    public ClaimPayment(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    public String getBankName() { return bankName; }
    public String getAccountNumber() { return accountNumber; }

    public void setBankName(String v) { this.bankName = v; }
    public void setAccountNumber(String v) { this.accountNumber = v; }
}
