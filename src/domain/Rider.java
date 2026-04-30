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

    public enum RiderType {}

    public String getDescription() { return description; }
    public Double getDiscountRate() { return discountRate; }
    public List<Exclusion> getExclusions() { return exclusions; }
    public boolean isMandatory() { return mandatory; }
    public String getProvisionId() { return provisionId; }
    public String getRiderCode() { return riderCode; }
    public String getRiderId() { return riderId; }
    public String getRiderName() { return riderName; }
    public RiderType getRiderType() { return riderType; }
}
