package infra.external.fss.mock;

import infra.external.fss.FssService;

public class MockFssService implements FssService {

    @Override
    public boolean submitApprovalApplication(String productId) {
        System.out.printf("[MockFSS] Approval application submitted: productId=%s%n", productId);
        return true;
    }

    @Override
    public boolean submitSaleNotification(String productId) {
        System.out.printf("[MockFSS] Sale notification submitted: productId=%s%n", productId);
        return true;
    }

    @Override
    public ReviewResult getApprovalReviewResult(String productId) {
        return ReviewResult.APPROVED;
    }

    @Override
    public ReviewResult getSaleReviewResult(String productId) {
        return ReviewResult.APPROVED;
    }
}
