package infra.external.fss;

/**
 * 금융감독원(FSS) 외부 서비스 인터페이스
 * 상품 인가 신청 및 판매 신고를 처리
 */
public interface FssService {
    boolean submitApprovalApplication(String productId);
    boolean submitSaleNotification(String productId);
    ReviewResult getApprovalReviewResult(String productId);
    ReviewResult getSaleReviewResult(String productId);

    enum ReviewResult {
        APPROVED("승인"), REJECTED("거절"), SUPPLEMENT_REQUIRED("보완요청");

        private final String label;
        ReviewResult(String label) { this.label = label; }
        public String getLabel() { return label; }
    }
}
