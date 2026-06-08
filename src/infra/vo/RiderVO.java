package infra.vo;

public class RiderVO {
    public final String riderId;
    public final String riderCode;
    public final String riderName;
    public final String description;
    public final String riderType;
    public final boolean mandatory;
    public final double discountRate;

    public RiderVO(String riderId, String riderCode, String riderName, String description,
                   String riderType, boolean mandatory, double discountRate) {
        this.riderId      = riderId;
        this.riderCode    = riderCode;
        this.riderName    = riderName;
        this.description  = description;
        this.riderType    = riderType;
        this.mandatory    = mandatory;
        this.discountRate = discountRate;
    }
}
