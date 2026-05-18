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

    // ── 정적 팩토리: Coverage 마스터로부터 스냅샷 생성 ─────────
    public static ProductCoverage from(Coverage coverage, List<CoverageLimitOption> selectedOptions) {
        ProductCoverage pc = new ProductCoverage();
        pc.productCoverageId = "PC-" + coverage.getCoverageId();
        pc.coverageMasterId  = coverage.getCoverageId();
        pc.coverageName      = coverage.getCoverageName();
        pc.coverageType      = coverage.getCoverageType();
        pc.mandatory         = coverage.isMandatory();
        pc.limitOptions      = selectedOptions;
        return pc;
    }

    // Setters
    public void setProductCoverageId(String v)        { this.productCoverageId = v; }
    public void setProductId(String v)                { this.productId = v; }
    public void setCoverageMasterId(String v)         { this.coverageMasterId = v; }
    public void setCoverageName(String v)             { this.coverageName = v; }
    public void setCoverageType(Coverage.CoverageType v) { this.coverageType = v; }
    public void setMandatory(boolean v)               { this.mandatory = v; }
    public void setLimitOptions(List<CoverageLimitOption> v) { this.limitOptions = v; }

    // Getters
    public String getCoverageMasterId()            { return coverageMasterId; }
    public String getCoverageName()                { return coverageName; }
    public Coverage.CoverageType getCoverageType() { return coverageType; }
    public List<CoverageLimitOption> getLimitOptions() { return limitOptions; }
    public boolean isMandatory()                   { return mandatory; }
    public String getProductCoverageId()           { return productCoverageId; }
    public String getProductId()                   { return productId; }
}
