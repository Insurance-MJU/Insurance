package domain;

import java.io.Serializable;

public class SelectedRider implements Serializable {
    private static final long serialVersionUID = 1L;
    private double discountRate;
    private String riderCode;
    private String riderId;
    private String riderName;

    public double getDiscountRate() { return discountRate; }
    public String getRiderCode() { return riderCode; }
    public String getRiderId() { return riderId; }
    public String getRiderName() { return riderName; }
}
