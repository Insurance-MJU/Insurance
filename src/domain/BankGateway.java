package domain;

import infra.dao.BankDao;

public class BankGateway {
    private final BankDao dao;

    public BankGateway(BankDao dao) {
        this.dao = dao;
    }

    public AccountVerification verifyAccount(String bankName, String accountNo) {
        return dao.verifyAccount(bankName, accountNo);
    }

    public TransferResult transfer(String bankName, String accountNo, long amount) {
        return dao.transfer(bankName, accountNo, amount);
    }
}
