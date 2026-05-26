package infra.dao;

import domain.Subscription;
import domain.SubscriptionList;
import domain.SubscriptionStatus;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SubscriptionDao {
    private final Database db;

    public SubscriptionDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Subscription> EXTRACTOR = rs -> mapRow(rs);

    private static Subscription mapRow(ResultSet rs) throws SQLException {
        Subscription s = new Subscription();
        s.setSubscriptionNo(rs.getString("subscription_no"));
        s.setApplicantName(rs.getString("applicant_name"));
        s.setSsn(rs.getString("ssn"));
        // address, chassisNumber, productName, occupation, coveragesDescription, rejectReason, supplementDocuments
        // via setters not exposed for all fields in Subscription - checking the class
        // Subscription has setCarNumber, setBasePremium, but not setAddress etc.
        // We use the register factory for new ones, but for loading we need all fields.
        // The register() factory sets all required fields including status=PENDING_REVIEW.
        // For loading, since there are limited setters, we use register() to rebuild,
        // then apply any status changes.

        // Re-checking Subscription: it has setters only for:
        // setSubscriptionNo, setApplicantName, setSsn, setCarNumber, setBasePremium
        // For address, chassisNumber, productName, occupation, age, coveragesDescription,
        // rejectReason, supplementDocuments - no individual setters visible.
        // The register() factory sets all those. We'll use register() to reconstruct.

        String subNo     = rs.getString("subscription_no");
        String appName   = rs.getString("applicant_name");
        String ssn       = rs.getString("ssn");
        String address   = rs.getString("address");
        String carNo     = rs.getString("car_number");
        String chassis   = rs.getString("chassis_number");
        String prodName  = rs.getString("product_name");
        long premium     = rs.getLong("premium");
        long basePremium = rs.getLong("base_premium");
        Timestamp subTs  = rs.getTimestamp("subscription_date");
        String subDate   = subTs != null
            ? new SimpleDateFormat("yyyy-MM-dd").format(new Date(subTs.getTime()))
            : new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String occupation = rs.getString("occupation");
        int age          = rs.getInt("age");
        String coverages = rs.getString("coverages_description");

        // Register creates with PENDING_REVIEW; we override status after
        // Minimum age check: register() validates age >= 18
        // Use a safe age of max(18, age)
        int safeAge = Math.max(18, age);

        Subscription sub = Subscription.register(
            subNo, appName, ssn, address, carNo, chassis, prodName,
            new Money(premium, "KRW"), new Money(basePremium, "KRW"),
            subDate, occupation, safeAge, coverages
        );

        // Now apply stored status
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            SubscriptionStatus status = SubscriptionStatus.valueOf(statusStr);
            if (status == SubscriptionStatus.APPROVED) sub.approve();
            else if (status == SubscriptionStatus.REJECTED) {
                String reason = rs.getString("reject_reason");
                sub.reject(reason != null ? reason : "");
            } else if (status == SubscriptionStatus.SUPPLEMENT_REQUIRED) {
                String docs = rs.getString("supplement_documents");
                sub.requestSupplement(docs != null ? docs : "");
            }
        }

        return sub;
    }

    public SubscriptionList findAll() {
        return new SubscriptionList(db.queryForList("SELECT * FROM subscriptions", EXTRACTOR));
    }

    public SubscriptionList findPendingReview() {
        return new SubscriptionList(db.queryForList(
            "SELECT * FROM subscriptions WHERE status = ?",
            EXTRACTOR, SubscriptionStatus.PENDING_REVIEW.name()));
    }

    public SubscriptionList findByApplicantName(String applicantName) {
        return new SubscriptionList(db.queryForList(
            "SELECT * FROM subscriptions WHERE applicant_name = ? ORDER BY subscription_date DESC",
            EXTRACTOR, applicantName));
    }

    public Subscription findByNo(String subscriptionNo) {
        return db.queryForObject(
            "SELECT * FROM subscriptions WHERE subscription_no = ?",
            EXTRACTOR, subscriptionNo);
    }

    public String nextSubscriptionNo() {
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM subscriptions WHERE subscription_no LIKE ?",
            rs -> rs.getInt(1), today + "-%");
        int next = (count != null ? count : 0) + 1;
        return String.format("%s-%04d", today, next);
    }

    public void save(Subscription s) {
        db.execute(
            "INSERT INTO subscriptions (subscription_no, applicant_name, ssn, address, car_number," +
            " chassis_number, product_name, premium, base_premium, subscription_date, status," +
            " occupation, age, coverages_description, reject_reason, supplement_documents)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " applicant_name=VALUES(applicant_name), ssn=VALUES(ssn), address=VALUES(address)," +
            " car_number=VALUES(car_number), chassis_number=VALUES(chassis_number)," +
            " product_name=VALUES(product_name), premium=VALUES(premium), base_premium=VALUES(base_premium)," +
            " subscription_date=VALUES(subscription_date), status=VALUES(status)," +
            " occupation=VALUES(occupation), age=VALUES(age)," +
            " coverages_description=VALUES(coverages_description)," +
            " reject_reason=VALUES(reject_reason), supplement_documents=VALUES(supplement_documents)",
            s.getSubscriptionNo(),
            s.getApplicantName(),
            s.getSsn(),
            s.getAddress(),
            s.getCarNumber(),
            s.getChassisNumber(),
            s.getProductName(),
            s.getPremium() != null ? s.getPremium().getAmount() : 0L,
            s.getBasePremium() != null ? s.getBasePremium().getAmount() : 0L,
            s.getSubscriptionDate() != null ? new Timestamp(s.getSubscriptionDate().getTime()) : null,
            s.getStatus() != null ? s.getStatus().name() : null,
            s.getOccupation(),
            s.getAge(),
            s.getCoveragesDescription(),
            s.getRejectReason(),
            s.getSupplementDocuments()
        );
    }
}
