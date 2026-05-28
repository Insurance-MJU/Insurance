package infra.external.bank.mock;

import infra.external.bank.BankService;
import infra.external.bank.dto.*;

import java.util.UUID;

public class MockBankService implements BankService {

    @Override
    public AccountVerifyResponse verifyAccount(AccountVerifyRequest request) {
        return new AccountVerifyResponse(true, "홍길동");
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.printf("[MockBank] Transfer: bank=%s account=%s amount=%,d → txId=%s%n",
                request.bankName(), request.accountNo(), request.amount(), txId);
        return new TransferResponse(true, txId);
    }
}
