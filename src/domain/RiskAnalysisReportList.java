package domain;

import infra.dao.RiskAnalysisReportDao;

import java.util.Collections;
import java.util.List;

public class RiskAnalysisReportList {
    private final RiskAnalysisReportDao dao;
    private final List<RiskAnalysisReport> reports;

    public RiskAnalysisReportList(RiskAnalysisReportDao dao) {
        this.dao = dao;
        this.reports = Collections.emptyList();
    }

    public RiskAnalysisReportList(List<RiskAnalysisReport> reports) {
        this.dao = null;
        this.reports = Collections.unmodifiableList(reports);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public RiskAnalysisReport findBySubscriptionNo(String subscriptionNo) {
        return dao.findBySubscriptionNo(subscriptionNo);
    }

    public void save(RiskAnalysisReport report) {
        dao.save(report);
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<RiskAnalysisReport> getAll() { return reports; }
    public boolean isEmpty() { return reports.isEmpty(); }
    public int size() { return reports.size(); }
}
