package domain;

import domain.common.Money;
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
    public static java.util.List<Subscription> findAll()            { return infra.dao.SubscriptionDao.getInstance().findAll(); }
    public static java.util.List<Subscription> findPendingReview()  { return infra.dao.SubscriptionDao.getInstance().findPendingReview(); }
    public static Subscription findByNo(String subscriptionNo)      { return infra.dao.SubscriptionDao.getInstance().findByNo(subscriptionNo); }
    public static void save(Subscription s)                         { infra.dao.SubscriptionDao.getInstance().save(s); }

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
