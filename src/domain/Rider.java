package domain;

public class Rider {
    private String description;
    private Double discountRate;
    private boolean mandatory;
    private String riderCode;
    private String riderId;
    private String riderName;
    private RiderType riderType;

    // Setters
    public void setRiderId(String v)       { this.riderId = v; }
    public void setRiderCode(String v)     { this.riderCode = v; }
    public void setRiderName(String v)     { this.riderName = v; }
    public void setDescription(String v)   { this.description = v; }
    public void setDiscountRate(Double v)  { this.discountRate = v; }
    public void setMandatory(boolean v)    { this.mandatory = v; }
    public void setRiderType(RiderType v)  { this.riderType = v; }

    // Getters
    public String getDescription()        { return description; }
    public Double getDiscountRate()       { return discountRate; }
    public boolean isMandatory()          { return mandatory; }
    public String getRiderCode()          { return riderCode; }
    public String getRiderId()            { return riderId; }
    public String getRiderName()          { return riderName; }
    public RiderType getRiderType()       { return riderType; }
}
