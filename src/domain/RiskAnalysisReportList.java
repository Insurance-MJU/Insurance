package domain;

import domain.common.Money;
import infra.dao.RiskAnalysisReportDao;
import infra.vo.RiskAnalysisReportVO;

import java.util.Collections;
import java.util.List;

public class RiskAnalysisReportList {
    private final RiskAnalysisReportDao dao;
    private final List<RiskAnalysisReport> reports;

    public RiskAnalysisReportList(RiskAnalysisReportDao dao) {
        this.dao     = dao;
        this.reports = Collections.emptyList();
    }

    public RiskAnalysisReportList(List<RiskAnalysisReport> reports) {
        this.dao     = null;
        this.reports = Collections.unmodifiableList(reports);
    }

    private static RiskAnalysisReport toDomain(RiskAnalysisReportVO vo) {
        if (vo == null) return null;
        return RiskAnalysisReport.fromStorage(
            vo.subscriptionNo,
            vo.riskScore, vo.riskGrade,
            vo.accidentScore, vo.drivingExpScore, vo.creditGradeScore, vo.trafficViolationScore,
            vo.surchargeRate,
            new Money(vo.basePremium, "KRW"),
            new Money(vo.surchargeAmount, "KRW"),
            new Money(vo.totalPremium, "KRW"),
            vo.reviewGuide, vo.reviewerName, vo.reviewDate, vo.reviewOpinion
        );
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public RiskAnalysisReport findBySubscriptionNo(String subscriptionNo) {
        return toDomain(dao.findBySubscriptionNo(subscriptionNo));
    }

    public void save(RiskAnalysisReport report) {
        dao.save(report);
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<RiskAnalysisReport> getAll() { return reports; }
    public boolean isEmpty() { return reports.isEmpty(); }
    public int size() { return reports.size(); }
}
