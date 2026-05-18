package domain;

import domain.common.Money;
import domain.exception.ValidationException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Subscription implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subscriptionNo;
    private String applicantName;
    private String ssn;
    private String address;
    private String carNumber;
    private String chassisNumber;
    private String productName;
    private Money premium;
    private Money basePremium;
    private Date subscriptionDate;
    private SubscriptionStatus status;
    private String occupation;
    private int age;
    private String coveragesDescription;
    private String rejectReason;
    private String supplementDocuments;

    // ── 정적 팩토리 ───────────────────────────────────────────
    public static Subscription register(
            String subscriptionNo, String applicantName, String ssn,
            String address, String carNumber, String chassisNumber,
            String productName, Money premium, Money basePremium,
            String subscriptionDate, String occupation, int age,
            String coveragesDescription) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (applicantName == null || applicantName.isBlank()) errors.add("신청자 이름은 필수입니다");
        if (ssn == null || ssn.isBlank())                     errors.add("주민등록번호는 필수입니다");
        if (carNumber == null || carNumber.isBlank())         errors.add("차량번호는 필수입니다");
        if (productName == null || productName.isBlank())     errors.add("상품명은 필수입니다");
        if (premium == null || premium.getAmount() <= 0)      errors.add("보험료는 0보다 커야 합니다");
        if (age < 18)                                         errors.add("가입 가능 연령은 18세 이상입니다");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        Subscription s = new Subscription();
        s.subscriptionNo       = subscriptionNo;
        s.applicantName        = applicantName;
        s.ssn                  = ssn;
        s.address              = address;
        s.carNumber            = carNumber;
        s.chassisNumber        = chassisNumber;
        s.productName          = productName;
        s.premium              = premium;
        s.basePremium          = basePremium;
        try { s.subscriptionDate = new SimpleDateFormat("yyyy-MM-dd").parse(subscriptionDate); } catch (Exception e) { s.subscriptionDate = new Date(); }
        s.status               = SubscriptionStatus.PENDING_REVIEW;
        s.occupation           = occupation;
        s.age                  = age;
        s.coveragesDescription = coveragesDescription;
        return s;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────────
    public void approve() {
        this.status = SubscriptionStatus.APPROVED;
    }

    public void reject(String reason) {
        this.status = SubscriptionStatus.REJECTED;
        this.rejectReason = reason;
    }

    public void requestSupplement(String documents) {
        this.status = SubscriptionStatus.SUPPLEMENT_REQUIRED;
        this.supplementDocuments = documents;
    }

    public boolean isPendingReview() {
        return this.status == SubscriptionStatus.PENDING_REVIEW;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public static String nextSubscriptionNo()                        { return infra.dao.SubscriptionDao.getInstance().nextSubscriptionNo(); }
    public static java.util.List<Subscription> findAll()            { return infra.dao.SubscriptionDao.getInstance().findAll(); }
    public static java.util.List<Subscription> findPendingReview()  { return infra.dao.SubscriptionDao.getInstance().findPendingReview(); }
    public static Subscription findByNo(String subscriptionNo)      { return infra.dao.SubscriptionDao.getInstance().findByNo(subscriptionNo); }
    public static void save(Subscription s)                         { infra.dao.SubscriptionDao.getInstance().save(s); }

    // ── Setters ──────────────────────────────────────────────
    public void setSubscriptionNo(String v)      { this.subscriptionNo = v; }
    public void setApplicantName(String v)        { this.applicantName = v; }
    public void setSsn(String v)                  { this.ssn = v; }
    public void setCarNumber(String v)            { this.carNumber = v; }
    public void setBasePremium(Money v)           { this.basePremium = v; }

    // ── Getters ───────────────────────────────────────────────
    public String getSubscriptionNo()       { return subscriptionNo; }
    public String getApplicantName()        { return applicantName; }
    public String getSsn()                  { return ssn; }
    public String getAddress()              { return address; }
    public String getCarNumber()            { return carNumber; }
    public String getChassisNumber()        { return chassisNumber; }
    public String getProductName()          { return productName; }
    public Money getPremium()               { return premium; }
    public Money getBasePremium()           { return basePremium; }
    public Date getSubscriptionDate()        { return subscriptionDate; }
    public String getSubscriptionDateDisplay() { return subscriptionDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(subscriptionDate) : ""; }
    public SubscriptionStatus getStatus()   { return status; }
    public String getOccupation()           { return occupation; }
    public int getAge()                     { return age; }
    public String getCoveragesDescription() { return coveragesDescription; }
    public String getRejectReason()         { return rejectReason; }
    public String getSupplementDocuments()  { return supplementDocuments; }
}
