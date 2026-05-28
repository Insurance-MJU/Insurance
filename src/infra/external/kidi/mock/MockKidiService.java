package infra.external.kidi.mock;

import infra.external.kidi.KidiService;

public class MockKidiService implements KidiService {

    @Override
    public boolean submitRateVerification(String productId) {
        System.out.printf("[MockKIDI] Rate verification submitted: productId=%s%n", productId);
        return true;
    }
}
