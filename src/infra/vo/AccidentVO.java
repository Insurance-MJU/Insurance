package infra.vo;

import java.util.Date;

public class AccidentVO {
    public final String accidentId;
    public final String userId;
    public final Date accidentDate;
    public final String reportedBy;
    public final String phone;
    public final String description;
    public final String accidentLocation;
    public final String accidentDetail;
    public final String documents;
    public final String contractId;
    public final String coverageDescription;
    public final long coverageLimit;
    public final long personalInjuryLimit;
    public final String vehicleInfo;
    public final long expectedRepairCost;
    public final String regionCode;
    public final String status;

    public AccidentVO(String accidentId, String userId, Date accidentDate, String reportedBy,
                      String phone, String description, String accidentLocation, String accidentDetail,
                      String documents, String contractId, String coverageDescription,
                      long coverageLimit, long personalInjuryLimit, String vehicleInfo,
                      long expectedRepairCost, String regionCode, String status) {
        this.accidentId          = accidentId;
        this.userId              = userId;
        this.accidentDate        = accidentDate;
        this.reportedBy          = reportedBy;
        this.phone               = phone;
        this.description         = description;
        this.accidentLocation    = accidentLocation;
        this.accidentDetail      = accidentDetail;
        this.documents           = documents;
        this.contractId          = contractId;
        this.coverageDescription = coverageDescription;
        this.coverageLimit       = coverageLimit;
        this.personalInjuryLimit = personalInjuryLimit;
        this.vehicleInfo         = vehicleInfo;
        this.expectedRepairCost  = expectedRepairCost;
        this.regionCode          = regionCode;
        this.status              = status;
    }
}
