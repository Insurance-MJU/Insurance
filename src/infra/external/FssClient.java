package infra.external;

// 금융감독원(FSS) 외부 시스템 Mock
public class FssClient {

    public boolean submitApprovalApplication(String productId) {
        return true;
    }

    public boolean submitSaleNotification(String productId) {
        return true;
    }

    public boolean isAvailable() {
        return true;
    }
}
