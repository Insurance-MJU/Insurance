package domain;

public class AccountVerification {
    private final boolean verified;
    private final String accountHolder;

    public AccountVerification(boolean verified, String accountHolder) {
        this.verified = verified;
        this.accountHolder = accountHolder;
    }

    public boolean isVerified()       { return verified; }
    public String  getAccountHolder() { return accountHolder; }
}
