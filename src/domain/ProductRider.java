package domain;

public class ProductRider {
    private Double discountRate;
    private String productId;
    private String productRiderId;
    private String riderCode;
    private String riderId;
    private String riderName;

    // Setters
    public void setDiscountRate(Double v)     { this.discountRate = v; }
    public void setProductId(String v)        { this.productId = v; }
    public void setProductRiderId(String v)   { this.productRiderId = v; }
    public void setRiderCode(String v)        { this.riderCode = v; }
    public void setRiderId(String v)          { this.riderId = v; }
    public void setRiderName(String v)        { this.riderName = v; }

    // Getters
    public Double getDiscountRate()    { return discountRate; }
    public String getProductId()       { return productId; }
    public String getProductRiderId()  { return productRiderId; }
    public String getRiderCode()       { return riderCode; }
    public String getRiderId()         { return riderId; }
    public String getRiderName()       { return riderName; }
}
