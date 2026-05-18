package domain;

import domain.common.Money;
import domain.exception.ValidationException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Accident implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accidentId;
    private Date accidentDate;
    private String accidentDetail;
    private String accidentLocation;
    private String reportedBy;
    private String phone;
    private AccidentStatus status;
    private String description;
    private String documents;
    private String contractId;
    private String coverageDescription;
    private Money coverageLimit;
    private Money personalInjuryLimit;
    private String vehicleInfo;
    private Money expectedRepairCost;
    private String regionCode;
    private AccidentType accidentType;
    private SeverityLevel severityLevel;

    public Accident() {}

    public Accident(String accidentId, String accidentDate, String reportedBy, String phone,
                    String description, String accidentLocation, String accidentDetail,
                    String documents, String contractId, String coverageDescription,
                    Money coverageLimit, String vehicleInfo, AccidentStatus status) {
        this.accidentId = accidentId;
        try { this.accidentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(accidentDate); } catch (Exception e) { this.accidentDate = null; }
        this.reportedBy = reportedBy;
        this.phone = phone;
        this.description = description;
        this.accidentLocation = accidentLocation;
        this.accidentDetail = accidentDetail;
        this.documents = documents;
        this.contractId = contractId;
        this.coverageDescription = coverageDescription;
        this.coverageLimit = coverageLimit;
        this.vehicleInfo = vehicleInfo;
        this.status = status;
    }

    // ── 정적 팩토리: 고객 보험금 청구 접수 ───────────────────
    public static Accident report(String accidentId, String reportedBy, String phone,
                                   String accidentDate, String accidentLocation,
                                   String accidentDetail, String documents,
                                   Contract contract) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (reportedBy == null || reportedBy.isBlank())         errors.add("신고자 이름은 필수입니다");
        if (phone == null || phone.isBlank())                   errors.add("연락처는 필수입니다");
        if (accidentDate == null || accidentDate.isBlank())     errors.add("사고일시는 필수입니다");
        if (accidentLocation == null || accidentLocation.isBlank()) errors.add("사고장소는 필수입니다");
        if (contract == null)                                   errors.add("계약 정보는 필수입니다");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        Accident a = new Accident();
        a.accidentId          = accidentId;
        a.reportedBy          = reportedBy;
        a.phone               = phone;
        a.description         = "보험금 청구";
        try { a.accidentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(accidentDate); } catch (Exception e) { a.accidentDate = null; }
        a.accidentLocation    = accidentLocation;
        a.accidentDetail      = accidentDetail;
        a.documents           = documents;
        a.contractId          = contract.getContractId();
        a.coverageDescription = contract.getCoveragesDescription();
        a.coverageLimit       = parseMoneyString(contract.getCoverageLimit());
        a.vehicleInfo         = contract.getCarNumber();
        a.status              = AccidentStatus.PENDING;
        return a;
    }

    private static Money parseMoneyString(String s) {
        if (s == null || s.isEmpty()) return new Money(0, "KRW");
        long mult = s.contains("만") ? 10_000L : 1L;
        String digits = s.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return new Money(0, "KRW");
        return new Money(Long.parseLong(digits) * mult, "KRW");
    }

    // ── 비즈니스 메서드: 상태 전이 ────────────────────────────
    public void transferToCompensation() { this.status = AccidentStatus.TRANSFERRED; }
    public void startProcessing()        { this.status = AccidentStatus.IN_PROGRESS; }
    public void complete()               { this.status = AccidentStatus.CLOSED; }
    public boolean isPending()           { return status == AccidentStatus.PENDING; }

    public boolean updateAccidentDetail(String detail) { this.accidentDetail = detail; return true; }
    public boolean validateAccident() {
        return accidentDate != null && accidentLocation != null && !accidentLocation.isEmpty();
    }

    public int getCoverageLimitManwon() {
        if (coverageLimit == null) return 2000;
        return (int)(coverageLimit.getAmount() / 10_000);
    }

    public String getAccidentId() { return accidentId; }
    public Date getAccidentDate() { return accidentDate; }
    public String getAccidentDateDisplay() { return accidentDate != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(accidentDate) : ""; }
    public String getAccidentDetail() { return accidentDetail; }
    public String getAccidentLocation() { return accidentLocation; }
    public String getReportedBy() { return reportedBy; }
    public String getPhone() { return phone; }
    public AccidentStatus getStatus() { return status; }
    public String getStatusLabel() { return status != null ? status.getLabel() : ""; }
    public String getDescription() { return description; }
    public String getDocuments() { return documents; }
    public String getContractId() { return contractId; }
    public String getCoverageDescription() { return coverageDescription; }
    public Money getCoverageLimit() { return coverageLimit; }
    public Money getPersonalInjuryLimit() { return personalInjuryLimit; }
    public String getVehicleInfo() { return vehicleInfo; }
    public Money getExpectedRepairCost() { return expectedRepairCost; }
    public String getRegionCode() { return regionCode; }
    public AccidentType getAccidentType() { return accidentType; }
    public SeverityLevel getSeverityLevel() { return severityLevel; }

    // ── DAO 위임 ──────────────────────────────────────────────
    public static java.util.List<Accident> findByDateAndStatus(String date, String status) { return infra.dao.AccidentDao.getInstance().findByDateAndStatus(date, status); }
    public static java.util.List<Accident> findPendingAccidents()                          { return infra.dao.AccidentDao.getInstance().findPendingAccidents(); }
    public static Accident findById(String accidentId)                                     { return infra.dao.AccidentDao.getInstance().findById(accidentId); }
    public static Accident findByCustomerName(String name)                                 { return infra.dao.AccidentDao.getInstance().findByCustomerName(name); }
    public static String nextId()                                                          { return infra.dao.AccidentDao.getInstance().nextId(); }
    public void save()                                                                     { infra.dao.AccidentDao.getInstance().save(this); }

    public void setAccidentId(String v) { this.accidentId = v; }
    public void setAccidentDate(Date v) { this.accidentDate = v; }
    public void setAccidentDetail(String v) { this.accidentDetail = v; }
    public void setAccidentLocation(String v) { this.accidentLocation = v; }
    public void setReportedBy(String v) { this.reportedBy = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setStatus(AccidentStatus v) { this.status = v; }
    public void setDescription(String v) { this.description = v; }
    public void setDocuments(String v) { this.documents = v; }
    public void setContractId(String v) { this.contractId = v; }
    public void setCoverageDescription(String v) { this.coverageDescription = v; }
    public void setCoverageLimit(Money v) { this.coverageLimit = v; }
    public void setPersonalInjuryLimit(Money v) { this.personalInjuryLimit = v; }
    public void setVehicleInfo(String v) { this.vehicleInfo = v; }
    public void setExpectedRepairCost(Money v) { this.expectedRepairCost = v; }
    public void setRegionCode(String v) { this.regionCode = v; }
    public void setAccidentType(AccidentType v) { this.accidentType = v; }
    public void setSeverityLevel(SeverityLevel v) { this.severityLevel = v; }
}
