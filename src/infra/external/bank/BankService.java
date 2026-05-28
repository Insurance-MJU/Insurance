package infra.external.bank;

import infra.external.bank.dto.AccountVerifyRequest;
import infra.external.bank.dto.AccountVerifyResponse;
import infra.external.bank.dto.TransferRequest;
import infra.external.bank.dto.TransferResponse;

/**
 * 은행 외부 서비스 인터페이스
 * 실제 구현체는 금융결제원 오픈뱅킹 API와 연동
 */
public interface BankService {
    AccountVerifyResponse verifyAccount(AccountVerifyRequest request);
    TransferResponse transfer(TransferRequest request);
}
