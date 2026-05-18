package domain;

import java.io.Serializable;
import domain.common.Money;

public class SelectedCoverage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Money basePremium;
    private String coverageMasterId;
    private String coverageName;
    private Coverage.CoverageType coverageType;
    private Money limitAmount;
    private boolean mandatory;

    // ── 정적 팩토리: ProductCoverage 스냅샷으로부터 생성 ────────
    public static SelectedCoverage from(ProductCoverage pc) {
        SelectedCoverage sc = new SelectedCoverage();
        sc.coverageMasterId = pc.getCoverageMasterId();
        sc.coverageName     = pc.getCoverageName();
        sc.coverageType     = pc.getCoverageType();
        sc.mandatory        = pc.isMandatory();
        return sc;
    }

    // Setters
    public void setBasePremium(Money v)                  { this.basePremium = v; }
    public void setCoverageMasterId(String v)            { this.coverageMasterId = v; }
    public void setCoverageName(String v)                { this.coverageName = v; }
    public void setCoverageType(Coverage.CoverageType v) { this.coverageType = v; }
    public void setLimitAmount(Money v)                  { this.limitAmount = v; }
    public void setMandatory(boolean v)                  { this.mandatory = v; }

    // Getters
    public Money getBasePremium()              { return basePremium; }
    public String getCoverageMasterId()        { return coverageMasterId; }
    public String getCoverageName()            { return coverageName; }
    public Coverage.CoverageType getCoverageType() { return coverageType; }
    public Money getLimitAmount()              { return limitAmount; }
    public boolean isMandatory()               { return mandatory; }
}
