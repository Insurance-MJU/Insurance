package domain;

import java.io.Serializable;

public class SelectedRider implements Serializable {
    private static final long serialVersionUID = 1L;
    private double discountRate;
    private String riderCode;
    private String riderId;
    private String riderName;

    // ── 정적 팩토리: ProductRider 스냅샷으로부터 생성 ───────────
    public static SelectedRider from(ProductRider pr) {
        SelectedRider sr = new SelectedRider();
        sr.riderId      = pr.getRiderId();
        sr.riderCode    = pr.getRiderCode();
        sr.riderName    = pr.getRiderName();
        sr.discountRate = pr.getDiscountRate() != null ? pr.getDiscountRate() : 0.0;
        return sr;
    }

    // Setters
    public void setDiscountRate(double v) { this.discountRate = v; }
    public void setRiderCode(String v)    { this.riderCode = v; }
    public void setRiderId(String v)      { this.riderId = v; }
    public void setRiderName(String v)    { this.riderName = v; }

    // Getters
    public double getDiscountRate() { return discountRate; }
    public String getRiderCode()    { return riderCode; }
    public String getRiderId()      { return riderId; }
    public String getRiderName()    { return riderName; }
}
