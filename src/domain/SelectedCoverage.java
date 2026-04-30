package domain;

import domain.common.Money;

public class SelectedCoverage {
    private Money basePremium;
    private String coverageMasterId;
    private String coverageName;
    private Coverage.CoverageType coverageType;
    private Money limitAmount;
    private boolean mandatory;

    public Money getBasePremium() { return basePremium; }
    public String getCoverageMasterId() { return coverageMasterId; }
    public String getCoverageName() { return coverageName; }
    public Coverage.CoverageType getCoverageType() { return coverageType; }
    public Money getLimitAmount() { return limitAmount; }
    public boolean isMandatory() { return mandatory; }
}
