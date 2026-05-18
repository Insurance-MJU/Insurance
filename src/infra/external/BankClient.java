package infra.external;

/**
 * 외부 은행 API 연동 클라이언트 Mock.
 * 실제 환경에서는 금융결제원 오픈뱅킹 API를 호출한다.
 */
public class BankClient {

    private static final BankClient INSTANCE = new BankClient();
    public static BankClient getInstance() { return INSTANCE; }

    public static class VerificationResult {
        public final boolean verified;
        public final String accountHolder;

        VerificationResult(boolean verified, String accountHolder) {
            this.verified       = verified;
            this.accountHolder  = accountHolder;
        }
    }

    public boolean isAvailable() {
        return true;
    }

    /**
     * 예금주 실명 확인.
     * @return 검증 결과 (verified=true, 예금주명)
     */
    public VerificationResult verifyAccount(String bankName, String accountNo) {
        // Mock: 모든 유효 계좌는 검증 통과
        return new VerificationResult(true, "홍길동");
    }

    /**
     * 실시간 계좌 이체.
     * @return 이체 성공 여부
     */
    public boolean transfer(String bankName, String accountNo, long amount) {
        // Mock: 항상 성공
        return true;
    }
}
