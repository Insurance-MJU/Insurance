package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.SubscriptionVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SubscriptionDao {
    private final Database db;

    public SubscriptionDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<SubscriptionVO> EXTRACTOR = rs -> mapRow(rs);

    private static SubscriptionVO mapRow(ResultSet rs) throws SQLException {
        Timestamp subTs = rs.getTimestamp("subscription_date");
        return new SubscriptionVO(
            rs.getString("subscription_no"),
            rs.getString("user_id"),
            rs.getString("applicant_name"),
            rs.getString("ssn"),
            rs.getString("address"),
            rs.getString("car_number"),
            rs.getString("chassis_number"),
            rs.getString("product_name"),
            rs.getLong("premium"),
            rs.getLong("base_premium"),
            subTs != null ? new Date(subTs.getTime()) : null,
            rs.getString("status"),
            rs.getString("occupation"),
            rs.getInt("age"),
            rs.getString("coverages_description"),
            rs.getString("reject_reason"),
            rs.getString("supplement_documents")
        );
    }

    public List<SubscriptionVO> findAll() {
        return db.queryForList("SELECT * FROM subscriptions", EXTRACTOR);
    }

    public List<SubscriptionVO> findPendingReview() {
        return db.queryForList(
            "SELECT * FROM subscriptions WHERE status = 'PENDING_REVIEW'", EXTRACTOR);
    }

    public List<SubscriptionVO> findByApplicantName(String applicantName) {
        return db.queryForList(
            "SELECT * FROM subscriptions WHERE applicant_name = ? ORDER BY subscription_date DESC",
            EXTRACTOR, applicantName);
    }

    public List<SubscriptionVO> findByUserId(String userId) {
        return db.queryForList(
            "SELECT * FROM subscriptions WHERE user_id = ? ORDER BY subscription_date DESC",
            EXTRACTOR, userId);
    }

    public SubscriptionVO findByNo(String subscriptionNo) {
        return db.queryForObject(
            "SELECT * FROM subscriptions WHERE subscription_no = ?",
            EXTRACTOR, subscriptionNo);
    }

    public String nextSubscriptionNo() {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM subscriptions WHERE subscription_no LIKE ?",
            rs -> rs.getInt(1), today + "-%");
        return String.format("%s-%04d", today, (count != null ? count : 0) + 1);
    }

    public void save(SubscriptionVO vo) {
        db.execute(
            "INSERT INTO subscriptions (subscription_no, user_id, applicant_name, ssn, address, car_number," +
            " chassis_number, product_name, premium, base_premium, subscription_date, status," +
            " occupation, age, coverages_description, reject_reason, supplement_documents)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " user_id=VALUES(user_id), applicant_name=VALUES(applicant_name), ssn=VALUES(ssn), address=VALUES(address)," +
            " car_number=VALUES(car_number), chassis_number=VALUES(chassis_number)," +
            " product_name=VALUES(product_name), premium=VALUES(premium), base_premium=VALUES(base_premium)," +
            " subscription_date=VALUES(subscription_date), status=VALUES(status)," +
            " occupation=VALUES(occupation), age=VALUES(age)," +
            " coverages_description=VALUES(coverages_description)," +
            " reject_reason=VALUES(reject_reason), supplement_documents=VALUES(supplement_documents)",
            vo.subscriptionNo, vo.userId, vo.applicantName, vo.ssn, vo.address,
            vo.carNumber, vo.chassisNumber, vo.productName, vo.premium, vo.basePremium,
            vo.subscriptionDate != null ? new Timestamp(vo.subscriptionDate.getTime()) : null,
            vo.status, vo.occupation, vo.age, vo.coveragesDescription,
            vo.rejectReason, vo.supplementDocuments
        );
    }
}
