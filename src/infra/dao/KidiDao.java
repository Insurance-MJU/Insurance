package infra.dao;

import infra.external.kidi.KidiService;

public class KidiDao {
    private final KidiService kidiService;

    public KidiDao(KidiService kidiService) {
        this.kidiService = kidiService;
    }

    public boolean submitRateVerification(String productId) {
        return kidiService.submitRateVerification(productId);
    }
}
