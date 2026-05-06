package domain;

import java.io.Serializable;

public class Accident implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accidentId;
    private String accidentDate;
    private String accidentDetail;
    private String accidentLocation;
    private String reportedBy;
    private String phone;
    private String status;
    private String description;
    private String documents;
    private String contractId;
    private String coverageDescription;
    private String coverageLimit;
    private String personalInjuryLimit;
    private String vehicleInfo;
    private String expectedRepairCost;
    private String regionCode;

    public enum AccidentType {}
    public enum SeverityLevel {}

    public Accident() {}

    public Accident(String accidentId, String accidentDate, String reportedBy, String phone,
                    String description, String accidentLocation, String accidentDetail,
                    String documents, String contractId, String coverageDescription,
                    String coverageLimit, String vehicleInfo, String status) {
        this.accidentId = accidentId;
        this.accidentDate = accidentDate;
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
    public static Accident report(String accidentId, String reportedBy,
                                   String accidentDate, String accidentLocation,
                                   String accidentDetail, String documents,
                                   Contract contract) {
        Accident a = new Accident();
        a.accidentId          = accidentId;
        a.reportedBy          = reportedBy;
        a.phone               = "";
        a.description         = "보험금 청구";
        a.accidentDate        = accidentDate;
        a.accidentLocation    = accidentLocation;
        a.accidentDetail      = accidentDetail;
        a.documents           = documents;
        a.contractId          = contract.getContractId();
        a.coverageDescription = contract.getCoveragesDescription();
        a.coverageLimit       = "";
        a.vehicleInfo         = contract.getCarNumber();
        a.status              = "미처리";
        return a;
    }

    // ── 비즈니스 메서드: 상태 전이 ────────────────────────────
    public void transferToCompensation() { this.status = "보상팀 이관"; }
    public void startProcessing()        { this.status = "처리중"; }
    public void complete()               { this.status = "처리완료"; }
    public boolean isPending()           { return "미처리".equals(status); }

    public boolean updateAccidentDetail(String detail) { this.accidentDetail = detail; return true; }
    public boolean validateAccident()    { return accidentDate != null && !accidentDate.isEmpty()
                                              && accidentLocation != null && !accidentLocation.isEmpty(); }

    /** coverageLimit 문자열("2,000만원")에서 숫자(2000)만 추출 */
    public int getCoverageLimitManwon() {
        if (coverageLimit == null) return 2000;
        try {
            return Integer.parseInt(coverageLimit.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 2000;
        }
    }

    public String getAccidentId() { return accidentId; }
    public String getAccidentDate() { return accidentDate; }
    public String getAccidentDetail() { return accidentDetail; }
    public String getAccidentLocation() { return accidentLocation; }
    public String getReportedBy() { return reportedBy; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public String getDocuments() { return documents; }
    public String getContractId() { return contractId; }
    public String getCoverageDescription() { return coverageDescription; }
    public String getCoverageLimit() { return coverageLimit; }
    public String getPersonalInjuryLimit() { return personalInjuryLimit; }
    public String getVehicleInfo() { return vehicleInfo; }
    public String getExpectedRepairCost() { return expectedRepairCost; }
    public String getRegionCode() { return regionCode; }

    public void setAccidentId(String v) { this.accidentId = v; }
    public void setAccidentDate(String v) { this.accidentDate = v; }
    public void setAccidentDetail(String v) { this.accidentDetail = v; }
    public void setAccidentLocation(String v) { this.accidentLocation = v; }
    public void setReportedBy(String v) { this.reportedBy = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setStatus(String v) { this.status = v; }
    public void setDescription(String v) { this.description = v; }
    public void setDocuments(String v) { this.documents = v; }
    public void setContractId(String v) { this.contractId = v; }
    public void setCoverageDescription(String v) { this.coverageDescription = v; }
    public void setCoverageLimit(String v) { this.coverageLimit = v; }
    public void setPersonalInjuryLimit(String v) { this.personalInjuryLimit = v; }
    public void setVehicleInfo(String v) { this.vehicleInfo = v; }
    public void setExpectedRepairCost(String v) { this.expectedRepairCost = v; }
    public void setRegionCode(String v) { this.regionCode = v; }
}
