package domain;

public class Accident {
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
    private String vehicleInfo;

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

    public String getAccidentInfo() { return null; }
    public boolean updateAccidentDetail(String detail) { this.accidentDetail = detail; return true; }
    public boolean validateAccident() { return false; }

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
    public String getVehicleInfo() { return vehicleInfo; }

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
    public void setVehicleInfo(String v) { this.vehicleInfo = v; }
}
