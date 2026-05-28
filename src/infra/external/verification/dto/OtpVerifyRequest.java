package infra.external.verification.dto;

public record OtpVerifyRequest(String sessionId, String otp) {}
