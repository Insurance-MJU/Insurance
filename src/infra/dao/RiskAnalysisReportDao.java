package infra.dao;

import domain.RiskAnalysisReport;
import infra.util.FileStore;
import java.util.*;

public class RiskAnalysisReportDao {
    private static final RiskAnalysisReportDao INSTANCE = new RiskAnalysisReportDao();
    public static RiskAnalysisReportDao getInstance() { return INSTANCE; }

    private static final List<RiskAnalysisReport> STORE;
    static {
        List<RiskAnalysisReport> loaded = FileStore.load("risk_analysis.dat");
        STORE = (loaded != null) ? loaded : new ArrayList<>();
    }

    public RiskAnalysisReport findBySubscriptionNo(String subscriptionNo) {
        return STORE.stream().filter(r -> r.getSubscriptionNo().equals(subscriptionNo)).findFirst().orElse(null);
    }

    public void save(RiskAnalysisReport report) {
        STORE.removeIf(r -> r.getSubscriptionNo().equals(report.getSubscriptionNo()));
        STORE.add(report);
        FileStore.save("risk_analysis.dat", STORE);
    }
}
