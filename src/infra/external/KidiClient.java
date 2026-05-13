package infra.external;

// 보험개발원(KIDI) 외부 시스템 Mock
public class KidiClient {

    public boolean submitRateVerification(String productId) {
        return true;
    }

    public boolean isAvailable() {
        return true;
    }
}
