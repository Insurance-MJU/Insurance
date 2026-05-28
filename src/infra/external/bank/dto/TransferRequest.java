package infra.external.bank.dto;

public record TransferRequest(String bankName, String accountNo, long amount) {}
