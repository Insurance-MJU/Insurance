package domain;

import java.util.List;

public class Rider {
    private String description;
    private Double discountRate;
    private List<Exclusion> exclusions;
    private boolean mandatory;
    private String provisionId;
    private String riderCode;
    private String riderId;
    private String riderName;
    private RiderType riderType;

    public enum RiderType { DISCOUNT, EXTRA_COVERAGE, MILEAGE, SAFETY }

    // Setters
    public void setRiderId(String v)       { this.riderId = v; }
    public void setRiderCode(String v)     { this.riderCode = v; }
    public void setRiderName(String v)     { this.riderName = v; }
    public void setDescription(String v)   { this.description = v; }
    public void setDiscountRate(Double v)  { this.discountRate = v; }
    public void setMandatory(boolean v)    { this.mandatory = v; }
    public void setProvisionId(String v)   { this.provisionId = v; }
    public void setRiderType(RiderType v)  { this.riderType = v; }
    public void setExclusions(List<Exclusion> v) { this.exclusions = v; }

    // Getters
    public String getDescription()        { return description; }
    public Double getDiscountRate()       { return discountRate; }
    public List<Exclusion> getExclusions(){ return exclusions; }
    public boolean isMandatory()          { return mandatory; }
    public String getProvisionId()        { return provisionId; }
    public String getRiderCode()          { return riderCode; }
    public String getRiderId()            { return riderId; }
    public String getRiderName()          { return riderName; }
    public RiderType getRiderType()       { return riderType; }
}
