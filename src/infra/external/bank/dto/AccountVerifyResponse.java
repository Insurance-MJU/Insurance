package infra.external.bank.dto;

public record AccountVerifyResponse(boolean verified, String accountHolder) {}
