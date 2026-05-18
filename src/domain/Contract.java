package domain;

import domain.common.Money;
import infra.util.FileStore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;

    // ── 정적 저장소 ───────────────────────────────────────────
    private static final List<Contract> STORE;
    private static final SimpleDateFormat SDF_STORE = new SimpleDateFormat("yyyy-MM-dd");

    static {
        List<Contract> loaded = FileStore.load("contracts.dat");
        if (loaded != null) {
            STORE = loaded;
        } else {
            STORE = new ArrayList<>();
            initDefaults();
        }
    }

    private static void initDefaults() {
        STORE.add(build("IN-2026-001", "CNT-20240315-001",
            "MZ세대 다이렉트 개인용자동차보험", Status.ACTIVE,
            "2026-04-01", "2026-04-01", "2027-04-01",
            2_509_200L, "대인배상I, 대인배상II, 대물배상", "마일리지 특약",
            "64마0866", "박수현"));
        STORE.add(build("IN-2025-002", "CNT-20240520-002",
            "MZ세대 다이렉트 개인용자동차보험", Status.ACTIVE,
            "2025-06-15", "2025-06-15", "2026-06-15",
            1_980_000L, "대인배상I, 대인배상II, 대물배상, 자기차량손해", "블랙박스 할인특약",
            "12가3456", "김직원"));
        STORE.add(build("IN-2023-003", "CNT-20231210-003",
            "MZ세대 다이렉트 개인용자동차보험", Status.EXPIRED,
            "2023-12-10", "2023-12-10", "2024-12-10",
            2_100_000L, "대인배상I, 대물배상, 자기차량손해", "없음",
            "56다9012", "이영희"));
        FileStore.save("contracts.dat", STORE);
    }

    private static Contract build(String policyNo, String contractId, String productName,
                                   Status status, String issueDate, String startDate, String endDate,
                                   long premiumAmount, String coverages, String riders,
                                   String carNumber, String holderName) {
        Party holder = new Party();
        holder.setPartyId("PARTY-" + contractId);
        holder.setName(holderName);

        Contract c = new Contract();
        c.policyNo             = policyNo;
        c.contractId           = contractId;
        c.productName          = productName;
        c.status               = status;
        c.policyholder         = holder;
        c.premium              = new Money(premiumAmount, "KRW");
        c.carNumber            = carNumber;
        c.coveragesDescription = coverages;
        c.ridersDescription    = riders;
        try {
            c.issueDate = SDF_STORE.parse(issueDate);
            c.startDate = SDF_STORE.parse(startDate);
            c.endDate   = SDF_STORE.parse(endDate);
        } catch (Exception ignored) {}
        return c;
    }

    public static List<Contract> findAll() {
        return Collections.unmodifiableList(STORE);
    }

    public static Contract findByPolicyNo(String policyNo) {
        return STORE.stream()
            .filter(c -> c.getPolicyNo().equals(policyNo))
            .findFirst().orElse(null);
    }

    public static List<Contract> findByCondition(String holderName, String periodChoice, String statusChoice) {
        String cutoff1 = LocalDate.now().minusYears(1).toString();
        String cutoff3 = LocalDate.now().minusYears(3).toString();
        return STORE.stream()
            .filter(c -> holderName.isEmpty()
                || (c.policyholder != null && holderName.equals(c.policyholder.getName())))
            .filter(c -> {
                if ("2".equals(periodChoice)) return c.getIssueDateString().compareTo(cutoff1) >= 0;
                if ("3".equals(periodChoice)) return c.getIssueDateString().compareTo(cutoff3) >= 0;
                return true;
            })
            .filter(c -> {
                if ("1".equals(statusChoice)) return c.status == Status.ACTIVE;
                if ("2".equals(statusChoice)) return c.status == Status.EXPIRED;
                if ("3".equals(statusChoice)) return c.status == Status.CANCELLED;
                return true;
            })
            .collect(Collectors.toList());
    }

    public static void save(Contract contract) {
        STORE.removeIf(c -> c.contractId.equals(contract.contractId));
        STORE.add(contract);
        FileStore.save("contracts.dat", STORE);
    }

    public static String nextPolicyNo() {
        return String.format("IN-2026-%03d", STORE.size() + 1);
    }

    public static String nextContractId() {
        return String.format("CNT-%d-%03d", LocalDate.now().getYear(), STORE.size() + 1);
    }
    private List<Party> beneficiaries;
    private String contractId;
    private Date endDate;
    private Insured insured;
    private Date issueDate;
    private Party namedInsured;
    private Party policyholder;
    private String policyNo;
    private Money premium;
    private Product product;
    private String productId;
    private List<SelectedCoverage> selectedCoverages;
    private List<SelectedRider> selectedRiders;
    private Date startDate;
    private Status status;
    private String carNumber;
    private String coveragesDescription;
    private String coverageLimit;
    private String ridersDescription;
    private String productName;

    public enum Status {
        ACTIVE, EXPIRED, CANCELLED;

        public String getLabel() {
            switch (this) {
                case ACTIVE:    return "유지중";
                case EXPIRED:   return "만기";
                case CANCELLED: return "해지";
                default:        return "";
            }
        }
    }

    // ── 정적 팩토리: 신규 계약 발행 ──────────────────────────
    public static Contract issue(String policyNo, String contractId,
                                  Product product, Party policyholder, Insured insured,
                                  Money premium, String carNumber,
                                  List<SelectedCoverage> selectedCoverages,
                                  List<SelectedRider> selectedRiders) {
        Contract c = new Contract();
        c.policyNo           = policyNo;
        c.contractId         = contractId;
        c.product            = product;
        c.productId          = product != null ? product.getProductId() : null;
        c.productName        = product != null ? product.getProductName() : null;
        c.policyholder       = policyholder;
        c.insured            = insured;
        c.premium            = premium;
        c.carNumber          = carNumber;
        c.selectedCoverages  = selectedCoverages;
        c.selectedRiders     = selectedRiders;
        c.coveragesDescription = selectedCoverages != null
            ? selectedCoverages.stream()
                .map(SelectedCoverage::getCoverageName)
                .reduce((a, b) -> a + ", " + b).orElse("")
            : "";
        c.ridersDescription  = selectedRiders != null
            ? selectedRiders.stream()
                .map(SelectedRider::getRiderName)
                .reduce((a, b) -> a + ", " + b).orElse("없음")
            : "없음";
        c.issueDate = new Date();
        c.startDate = new Date();
        c.activate();
        return c;
    }

    public void activate()   { this.status = Status.ACTIVE; }
    public void cancel()     { this.status = Status.CANCELLED; }
    public void mature()     { this.status = Status.EXPIRED; }
    public void reinstate()  { this.status = Status.ACTIVE; }
    public boolean isActive() { return this.status == Status.ACTIVE; }

    public String getStatusLabel() {
        return status != null ? status.getLabel() : "";
    }

    public String getIssueDateString() {
        if (issueDate == null) return "-";
        return new SimpleDateFormat("yyyy-MM-dd").format(issueDate);
    }

    public List<Party> getBeneficiaries()         { return beneficiaries; }
    public String getContractId()                 { return contractId; }
    public Date getEndDate()                      { return endDate; }
    public Insured getInsured()                   { return insured; }
    public Date getIssueDate()                    { return issueDate; }
    public Party getNamedInsured()                { return namedInsured; }
    public Party getPolicyholder()                { return policyholder; }
    public String getPolicyNo()                   { return policyNo; }
    public Money getPremium()                     { return premium; }
    public Product getProduct()                   { return product; }
    public String getProductId()                  { return productId; }
    public List<SelectedCoverage> getSelectedCoverages() { return selectedCoverages; }
    public List<SelectedRider> getSelectedRiders()       { return selectedRiders; }
    public Date getStartDate()                    { return startDate; }
    public Status getStatus()                     { return status; }
    public String getCarNumber()                  { return carNumber; }
    public String getCoveragesDescription()       { return coveragesDescription; }
    public String getCoverageLimit()              { return coverageLimit != null ? coverageLimit : ""; }
    public String getRidersDescription()          { return ridersDescription; }
    public String getProductName()                { return productName; }

    public void setBeneficiaries(List<Party> v)           { this.beneficiaries = v; }
    public void setContractId(String v)                   { this.contractId = v; }
    public void setEndDate(Date v)                        { this.endDate = v; }
    public void setInsured(Insured v)                     { this.insured = v; }
    public void setIssueDate(Date v)                      { this.issueDate = v; }
    public void setNamedInsured(Party v)                  { this.namedInsured = v; }
    public void setPolicyholder(Party v)                  { this.policyholder = v; }
    public void setPolicyNo(String v)                     { this.policyNo = v; }
    public void setPremium(Money v)                       { this.premium = v; }
    public void setProduct(Product v)                     { this.product = v; }
    public void setProductId(String v)                    { this.productId = v; }
    public void setSelectedCoverages(List<SelectedCoverage> v) { this.selectedCoverages = v; }
    public void setSelectedRiders(List<SelectedRider> v)       { this.selectedRiders = v; }
    public void setStartDate(Date v)                      { this.startDate = v; }
    public void setStatus(Status v)                       { this.status = v; }
    public void setCarNumber(String v)                    { this.carNumber = v; }
    public void setCoveragesDescription(String v)         { this.coveragesDescription = v; }
    public void setCoverageLimit(String v)                { this.coverageLimit = v; }
    public void setRidersDescription(String v)            { this.ridersDescription = v; }
    public void setProductName(String v)                  { this.productName = v; }
}
