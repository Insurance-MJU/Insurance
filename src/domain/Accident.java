package domain;

import domain.common.Money;
import infra.util.FileStore;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Accident implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accidentId;
    private Date accidentDate;
    private String accidentDetail;
    private String accidentLocation;
    private String reportedBy;
    private String phone;
    private String status;
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

    public enum AccidentType {
        COLLISION, REAR_END, SINGLE, HIT_AND_RUN, FIRE, FLOOD, THEFT, NATURAL_DISASTER
    }

    public enum SeverityLevel {
        MINOR, MODERATE, SEVERE, FATAL, TOTAL_LOSS
    }

    public Accident() {}

    public Accident(String accidentId, String accidentDate, String reportedBy, String phone,
                    String description, String accidentLocation, String accidentDetail,
                    String documents, String contractId, String coverageDescription,
                    Money coverageLimit, String vehicleInfo, String status) {
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
        a.status              = "미처리";
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
    public void transferToCompensation() { this.status = "보상팀 이관"; }
    public void startProcessing()        { this.status = "처리중"; }
    public void complete()               { this.status = "처리완료"; }
    public boolean isPending()           { return "미처리".equals(status); }

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
    public String getStatus() { return status; }
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

    public void setAccidentId(String v) { this.accidentId = v; }
    public void setAccidentDate(Date v) { this.accidentDate = v; }
    public void setAccidentDetail(String v) { this.accidentDetail = v; }
    public void setAccidentLocation(String v) { this.accidentLocation = v; }
    public void setReportedBy(String v) { this.reportedBy = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setStatus(String v) { this.status = v; }
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

    // ── 영속성 ────────────────────────────────────────────────
    private static final List<Accident> STORE;
    static {
        List<Accident> loaded = FileStore.load("accidents.dat");
        if (loaded != null) { STORE = loaded; }
        else { STORE = new ArrayList<>(); initDefaults(); }
    }
    private static void initDefaults() {
        Accident a1 = new Accident("ACC-2026-001", "2026-04-19 09:32", "홍길동", "010-1234-5678",
            "자동차 대물 사고", "서울 강남구 테헤란로", "신호 대기 중 후방 추돌 사고 발생",
            "사고현장사진.jpg,차량수리견적서.pdf", "CNT-20240315-001", "자동차 대물",
            new Money(20_000_000, "KRW"), "12가 3456 (현대 소나타)", "미처리");
        a1.setPersonalInjuryLimit(new Money(10_000_000, "KRW"));
        a1.setExpectedRepairCost(new Money(850_000, "KRW"));
        a1.setRegionCode("SEOUL-01");
        STORE.add(a1);

        Accident a2 = new Accident("ACC-2026-002", "2026-04-19 11:15", "김철수", "010-9876-5432",
            "차량 파손", "경기도 수원시 팔달구", "주차장 내 차량 문 충돌로 인한 파손",
            "차량파손사진.jpg,수리견적서.pdf", "CNT-20240520-002", "자기차량손해",
            new Money(30_000_000, "KRW"), "34나 5678 (기아 K5)", "미처리");
        a2.setPersonalInjuryLimit(new Money(20_000_000, "KRW"));
        a2.setExpectedRepairCost(new Money(1_200_000, "KRW"));
        a2.setRegionCode("GYEONGGI-01");
        STORE.add(a2);

        Accident a3 = new Accident("ACC-2026-003", "2026-04-18 14:20", "이영희", "010-5555-1234",
            "차량 전손", "인천시 부평구 경인로", "교차로 신호 위반으로 인한 정면 충돌",
            "사고사진.jpg,전손감정서.pdf", "CNT-20231210-003", "자기차량손해",
            new Money(50_000_000, "KRW"), "56다 9012 (현대 그랜저)", "처리중");
        a3.setPersonalInjuryLimit(new Money(30_000_000, "KRW"));
        a3.setExpectedRepairCost(new Money(3_500_000, "KRW"));
        a3.setRegionCode("INCHEON-01");
        STORE.add(a3);

        FileStore.save("accidents.dat", STORE);
    }

    public static List<Accident> findByDateAndStatus(String date, String status) {
        return STORE.stream()
            .filter(a -> a.accidentDate != null
                && new SimpleDateFormat("yyyy-MM-dd").format(a.accidentDate).startsWith(date))
            .filter(a -> status.isEmpty() || (a.status != null && a.status.equals(status)))
            .collect(Collectors.toList());
    }
    public static List<Accident> findPendingAccidents() {
        return STORE.stream().filter(a -> "미처리".equals(a.status)).collect(Collectors.toList());
    }
    public static Accident findById(String accidentId) {
        return STORE.stream().filter(a -> a.accidentId.equals(accidentId)).findFirst().orElse(null);
    }
    public static Accident findByCustomerName(String name) {
        return STORE.stream().filter(a -> a.reportedBy.equals(name)).findFirst().orElse(null);
    }
    public void save() {
        STORE.removeIf(a -> a.accidentId.equals(this.accidentId));
        STORE.add(this);
        FileStore.save("accidents.dat", STORE);
    }
    public static String nextId() {
        return String.format("ACC-2026-%03d", STORE.size() + 1);
    }
}
