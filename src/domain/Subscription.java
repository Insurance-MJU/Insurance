package domain;

import domain.common.Money;
import infra.util.FileStore;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Subscription implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        PENDING_REVIEW, APPROVED, REJECTED, SUPPLEMENT_REQUIRED;

        public String getLabel() {
            switch (this) {
                case PENDING_REVIEW:      return "심사대기중";
                case APPROVED:            return "인수승인";
                case REJECTED:            return "인수거절";
                case SUPPLEMENT_REQUIRED: return "서류보완요청";
                default:                  return "";
            }
        }
    }

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
    private Status status;
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
        s.status               = Status.PENDING_REVIEW;
        s.occupation           = occupation;
        s.age                  = age;
        s.coveragesDescription = coveragesDescription;
        return s;
    }

    // ── 비즈니스 메서드 ───────────────────────────────────────
    public void approve() {
        this.status = Status.APPROVED;
    }

    public void reject(String reason) {
        this.status = Status.REJECTED;
        this.rejectReason = reason;
    }

    public void requestSupplement(String documents) {
        this.status = Status.SUPPLEMENT_REQUIRED;
        this.supplementDocuments = documents;
    }

    public boolean isPendingReview() {
        return this.status == Status.PENDING_REVIEW;
    }

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
    public Status getStatus()               { return status; }
    public String getOccupation()           { return occupation; }
    public int getAge()                     { return age; }
    public String getCoveragesDescription() { return coveragesDescription; }
    public String getRejectReason()         { return rejectReason; }
    public String getSupplementDocuments()  { return supplementDocuments; }

    // ── 영속성 ────────────────────────────────────────────────
    private static final List<Subscription> STORE;
    static {
        List<Subscription> loaded = FileStore.load("subscriptions.dat");
        if (loaded != null) { STORE = loaded; }
        else { STORE = new ArrayList<>(); initDefaults(); }
    }
    private static void initDefaults() {
        STORE.add(Subscription.register(
            "20260401-0001", "박수현", "020101-3******",
            "서울시 강남구", "64마0866", "KMHCT41DBLU123",
            "MZ세대 다이렉트 차보험",
            new Money(2_907_200L, "KRW"), new Money(2_794_010L, "KRW"),
            "2026-04-01", "대학생", 24,
            "대인I/II, 대물 5억, 자상 1억, 무보험 2억, 자차 가입"
        ));
        FileStore.save("subscriptions.dat", STORE);
    }
    public static List<Subscription> findAll() { return new ArrayList<>(STORE); }
    public static List<Subscription> findPendingReview() {
        return STORE.stream().filter(Subscription::isPendingReview).collect(Collectors.toList());
    }
    public static Subscription findByNo(String subscriptionNo) {
        return STORE.stream().filter(s -> s.subscriptionNo.equals(subscriptionNo)).findFirst().orElse(null);
    }
    public static void save(Subscription subscription) {
        STORE.removeIf(s -> s.subscriptionNo.equals(subscription.subscriptionNo));
        STORE.add(subscription);
        FileStore.save("subscriptions.dat", STORE);
    }
}
