package domain;

import infra.dao.FssDao;
import infra.dao.KidiDao;

public class ProductApprovalGateway {
    private final FssDao fssDao;
    private final KidiDao kidiDao;

    public ProductApprovalGateway(FssDao fssDao, KidiDao kidiDao) {
        this.fssDao = fssDao;
        this.kidiDao = kidiDao;
    }

    public boolean submitRateVerification(String productId) {
        return kidiDao.submitRateVerification(productId);
    }

    public boolean submitApprovalApplication(String productId) {
        return fssDao.submitApprovalApplication(productId);
    }

    public ExternalReviewResult getApprovalReviewResult(String productId) {
        return fssDao.getApprovalReviewResult(productId);
    }

    public boolean submitSaleNotification(String productId) {
        return fssDao.submitSaleNotification(productId);
    }

    public ExternalReviewResult getSaleReviewResult(String productId) {
        return fssDao.getSaleReviewResult(productId);
    }
}
