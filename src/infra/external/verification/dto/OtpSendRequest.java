package infra.external.verification.dto;

public record OtpSendRequest(String name, String ssn, String phone, String method) {}
