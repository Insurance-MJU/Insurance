package infra.external;

// 금융감독원(FSS) 외부 시스템 Mock
public class FssClient {

    public enum ReviewResult {
        APPROVED("승인"),
        REJECTED("거절"),
        SUPPLEMENT_REQUIRED("보완요청");

        private final String label;
        ReviewResult(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public boolean submitApprovalApplication(String productId) {
        return true;
    }

    public boolean submitSaleNotification(String productId) {
        return true;
    }

    public boolean isAvailable() {
        return true;
    }

    // 인가 심사 결과 조회 (mock - 실제 연동 시 폴링/콜백으로 대체)
    public ReviewResult getApprovalReviewResult(String productId) {
        return ReviewResult.APPROVED;
    }

    // 판매 심사 결과 조회
    public ReviewResult getSaleReviewResult(String productId) {
        return ReviewResult.APPROVED;
    }
}
