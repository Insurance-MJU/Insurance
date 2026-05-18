package domain;

import java.io.Serializable;
import domain.common.Money;

public class SelectedCoverage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Money basePremium;
    private String coverageMasterId;
    private String coverageName;
    private CoverageType coverageType;
    private Deductible deductible;
    private Money limitAmount;
    private boolean mandatory;

    public Money getBasePremium()         { return basePremium; }
    public String getCoverageMasterId()   { return coverageMasterId; }
    public String getCoverageName()       { return coverageName; }
    public CoverageType getCoverageType() { return coverageType; }
    public Deductible getDeductible()     { return deductible; }
    public Money getLimitAmount()         { return limitAmount; }
    public boolean isMandatory()          { return mandatory; }

    public void setBasePremium(Money v)         { this.basePremium = v; }
    public void setCoverageMasterId(String v)   { this.coverageMasterId = v; }
    public void setCoverageName(String v)       { this.coverageName = v; }
    public void setCoverageType(CoverageType v) { this.coverageType = v; }
    public void setDeductible(Deductible v)     { this.deductible = v; }
    public void setLimitAmount(Money v)         { this.limitAmount = v; }
    public void setMandatory(boolean v)         { this.mandatory = v; }
}
