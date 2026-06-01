package infra.vo;

public class ProductRiderVO {
    public final String productRiderId;
    public final String productId;
    public final String riderId;
    public final String riderCode;
    public final String riderName;
    public final double discountRate;

    public ProductRiderVO(String productRiderId, String productId, String riderId,
                          String riderCode, String riderName, double discountRate) {
        this.productRiderId = productRiderId;
        this.productId      = productId;
        this.riderId        = riderId;
        this.riderCode      = riderCode;
        this.riderName      = riderName;
        this.discountRate   = discountRate;
    }
}
