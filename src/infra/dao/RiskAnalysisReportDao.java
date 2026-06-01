package infra.dao;

import domain.RiskAnalysisReport;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.RiskAnalysisReportVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RiskAnalysisReportDao {
    private final Database db;

    public RiskAnalysisReportDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<RiskAnalysisReportVO> EXTRACTOR = rs -> mapRow(rs);

    private static RiskAnalysisReportVO mapRow(ResultSet rs) throws SQLException {
        Timestamp reviewTs = rs.getTimestamp("review_date");
        return new RiskAnalysisReportVO(
            rs.getString("subscription_no"),
            rs.getDouble("risk_score"),
            rs.getInt("risk_grade"),
            rs.getDouble("accident_score"),
            rs.getDouble("driving_exp_score"),
            rs.getDouble("credit_grade_score"),
            rs.getDouble("traffic_violation_score"),
            rs.getDouble("surcharge_rate"),
            rs.getLong("base_premium"),
            rs.getLong("surcharge_amount"),
            rs.getLong("total_premium"),
            rs.getString("review_guide"),
            rs.getString("reviewer_name"),
            reviewTs != null ? new java.util.Date(reviewTs.getTime()) : null,
            rs.getString("review_opinion")
        );
    }

    public RiskAnalysisReportVO findBySubscriptionNo(String subscriptionNo) {
        return db.queryForObject(
            "SELECT * FROM risk_analysis_reports WHERE subscription_no = ?",
            EXTRACTOR, subscriptionNo);
    }

    public void save(RiskAnalysisReport report) {
        db.execute(
            "INSERT INTO risk_analysis_reports" +
            " (subscription_no, risk_score, risk_grade, accident_score, driving_exp_score," +
            " credit_grade_score, traffic_violation_score, surcharge_rate," +
            " base_premium, surcharge_amount, total_premium," +
            " review_guide, reviewer_name, review_date, review_opinion)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " risk_score=VALUES(risk_score), risk_grade=VALUES(risk_grade)," +
            " accident_score=VALUES(accident_score), driving_exp_score=VALUES(driving_exp_score)," +
            " credit_grade_score=VALUES(credit_grade_score), traffic_violation_score=VALUES(traffic_violation_score)," +
            " surcharge_rate=VALUES(surcharge_rate), base_premium=VALUES(base_premium)," +
            " surcharge_amount=VALUES(surcharge_amount), total_premium=VALUES(total_premium)," +
            " review_guide=VALUES(review_guide), reviewer_name=VALUES(reviewer_name)," +
            " review_date=VALUES(review_date), review_opinion=VALUES(review_opinion)",
            report.getSubscriptionNo(),
            report.getRiskScore(),
            report.getRiskGrade(),
            report.getAccidentScore(),
            report.getDrivingExpScore(),
            report.getCreditGradeScore(),
            report.getTrafficViolationScore(),
            report.getSurchargeRate(),
            report.getBasePremium()     != null ? report.getBasePremium().getAmount()     : 0L,
            report.getSurchargeAmount() != null ? report.getSurchargeAmount().getAmount() : 0L,
            report.getTotalPremium()    != null ? report.getTotalPremium().getAmount()    : 0L,
            report.getReviewGuide(),
            report.getReviewerName(),
            report.getReviewDate() != null ? new Timestamp(report.getReviewDate().getTime()) : null,
            report.getReviewOpinion()
        );
    }
}
