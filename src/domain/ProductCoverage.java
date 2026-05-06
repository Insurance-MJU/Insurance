package domain;

import java.io.Serializable;
import java.util.List;

public class ProductCoverage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String coverageMasterId;
    private String coverageName;
    private Coverage.CoverageType coverageType;
    private List<CoverageLimitOption> limitOptions;
    private boolean mandatory;
    private String productCoverageId;
    private String productId;

    public String getCoverageMasterId() { return coverageMasterId; }
    public String getCoverageName() { return coverageName; }
    public Coverage.CoverageType getCoverageType() { return coverageType; }
    public List<CoverageLimitOption> getLimitOptions() { return limitOptions; }
    public boolean isMandatory() { return mandatory; }
    public String getProductCoverageId() { return productCoverageId; }
    public String getProductId() { return productId; }
}
