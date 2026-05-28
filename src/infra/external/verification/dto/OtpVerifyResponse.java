package infra.external.verification.dto;

public record OtpVerifyResponse(boolean success, String verificationToken, String errorMessage) {
    public static OtpVerifyResponse ok(String token)    { return new OtpVerifyResponse(true,  token, null); }
    public static OtpVerifyResponse fail(String reason) { return new OtpVerifyResponse(false, null,  reason); }
}
