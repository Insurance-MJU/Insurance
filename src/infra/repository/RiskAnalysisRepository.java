package infra.repository;

import domain.RiskAnalysisReport;
import infra.util.FileStore;

import java.util.ArrayList;
import java.util.List;

public class RiskAnalysisRepository {
    private static final List<RiskAnalysisReport> STORE;

    static {
        List<RiskAnalysisReport> loaded = FileStore.load("risk_analysis.dat");
        STORE = (loaded != null) ? loaded : new ArrayList<>();
    }

    public static RiskAnalysisReport findBySubscriptionNo(String subscriptionNo) {
        return STORE.stream()
            .filter(r -> r.getSubscriptionNo().equals(subscriptionNo))
            .findFirst().orElse(null);
    }

    public static void save(RiskAnalysisReport report) {
        STORE.removeIf(r -> r.getSubscriptionNo().equals(report.getSubscriptionNo()));
        STORE.add(report);
        FileStore.save("risk_analysis.dat", STORE);
    }
}
