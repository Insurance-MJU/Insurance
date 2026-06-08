package infra.vo;

public class ProductCoverageVO {
    public final String productCoverageId;
    public final String productId;
    public final String coverageMasterId;
    public final String coverageName;
    public final String coverageType;
    public final boolean mandatory;

    public ProductCoverageVO(String productCoverageId, String productId, String coverageMasterId,
                             String coverageName, String coverageType, boolean mandatory) {
        this.productCoverageId = productCoverageId;
        this.productId         = productId;
        this.coverageMasterId  = coverageMasterId;
        this.coverageName      = coverageName;
        this.coverageType      = coverageType;
        this.mandatory         = mandatory;
    }
}
