package domain.product;

import domain.provision.Rider;

import java.io.Serializable;

public class ProductRider implements Serializable {
    private static final long serialVersionUID = 1L;
    private Double discountRate;
    private String productId;
    private String productRiderId;
    private String riderCode;
    private String riderId;
    private String riderName;

    // ── 정적 팩토리: Rider 마스터로부터 생성 ─────────────────
    public static ProductRider from(Rider rider) {
        ProductRider pr = new ProductRider();
        pr.productRiderId = "PR-" + rider.getRiderId();
        pr.riderId        = rider.getRiderId();
        pr.riderCode      = rider.getRiderCode();
        pr.riderName      = rider.getRiderName();
        pr.discountRate   = rider.getDiscountRate();
        return pr;
    }

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
