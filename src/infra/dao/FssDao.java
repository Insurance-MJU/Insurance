package infra.dao;

import domain.ExternalReviewResult;
import infra.external.fss.FssService;

public class FssDao {
    private final FssService fssService;

    public FssDao(FssService fssService) {
        this.fssService = fssService;
    }

    public boolean submitApprovalApplication(String productId) {
        return fssService.submitApprovalApplication(productId);
    }

    public ExternalReviewResult getApprovalReviewResult(String productId) {
        return toResult(fssService.getApprovalReviewResult(productId));
    }

    public boolean submitSaleNotification(String productId) {
        return fssService.submitSaleNotification(productId);
    }

    public ExternalReviewResult getSaleReviewResult(String productId) {
        return toResult(fssService.getSaleReviewResult(productId));
    }

    private ExternalReviewResult toResult(FssService.ReviewResult r) {
        switch (r) {
            case APPROVED:             return ExternalReviewResult.APPROVED;
            case REJECTED:             return ExternalReviewResult.REJECTED;
            case SUPPLEMENT_REQUIRED:  return ExternalReviewResult.SUPPLEMENT_REQUIRED;
            default:                   return ExternalReviewResult.REJECTED;
        }
    }
}
