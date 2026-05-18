package domain;

import java.io.Serializable;

public class ClaimPayment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bankName;
    private String accountNumber;

    public ClaimPayment() {}

    public ClaimPayment(String bankName, String accountNumber) {
        this.bankName      = bankName;
        this.accountNumber = accountNumber;
    }

    public static class AccountVerification {
        public final boolean verified;
        public final String accountHolder;
        AccountVerification(boolean verified, String accountHolder) {
            this.verified       = verified;
            this.accountHolder  = accountHolder;
        }
    }

    /** 예금주 실명 확인 (외부 은행 API 위임) */
    public static AccountVerification verifyAccount(String bank, String accountNo) {
        infra.external.BankClient.VerificationResult r =
            infra.external.BankClient.getInstance().verifyAccount(bank, accountNo);
        return new AccountVerification(r.verified, r.accountHolder);
    }

    /** 실시간 계좌 이체 (외부 은행 API 위임) */
    public static boolean transfer(String bank, String accountNo, long amount) {
        return infra.external.BankClient.getInstance().transfer(bank, accountNo, amount);
    }

    public String getBankName()      { return bankName; }
    public String getAccountNumber() { return accountNumber; }

    public void setBankName(String v)      { this.bankName = v; }
    public void setAccountNumber(String v) { this.accountNumber = v; }
}
