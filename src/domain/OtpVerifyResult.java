package domain;

public class OtpVerifyResult {
    private final boolean success;
    private final String verificationToken;
    private final String errorMessage;

    private OtpVerifyResult(boolean success, String verificationToken, String errorMessage) {
        this.success = success;
        this.verificationToken = verificationToken;
        this.errorMessage = errorMessage;
    }

    public static OtpVerifyResult ok(String token)     { return new OtpVerifyResult(true, token, null); }
    public static OtpVerifyResult fail(String message) { return new OtpVerifyResult(false, null, message); }

    public boolean isSuccess()           { return success; }
    public String  getVerificationToken() { return verificationToken; }
    public String  getErrorMessage()      { return errorMessage; }
}
