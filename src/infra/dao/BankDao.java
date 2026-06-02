package infra.dao;

import domain.AccountVerification;
import domain.TransferResult;
import infra.external.bank.BankService;
import infra.external.bank.dto.AccountVerifyRequest;
import infra.external.bank.dto.AccountVerifyResponse;
import infra.external.bank.dto.TransferRequest;
import infra.external.bank.dto.TransferResponse;

public class BankDao {
    private final BankService bankService;

    public BankDao(BankService bankService) {
        this.bankService = bankService;
    }

    public AccountVerification verifyAccount(String bankName, String accountNo) {
        AccountVerifyResponse r = bankService.verifyAccount(new AccountVerifyRequest(bankName, accountNo));
        return new AccountVerification(r.verified(), r.accountHolder());
    }

    public TransferResult transfer(String bankName, String accountNo, long amount) {
        infra.external.bank.dto.TransferResponse r =
            bankService.transfer(new TransferRequest(bankName, accountNo, amount));
        return new TransferResult(r.success(), r.transactionId());
    }
}
