package infra.external.bank.dto;

public record TransferResponse(boolean success, String transactionId) {}
