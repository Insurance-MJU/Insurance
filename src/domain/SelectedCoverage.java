package domain;

import java.io.Serializable;
import domain.common.Money;

public class SelectedCoverage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Money basePremium;
    private String coverageMasterId;
    private String coverageName;
    private CoverageType coverageType;
    private Money limitAmount;
    private boolean mandatory;

    public Money getBasePremium() { return basePremium; }
    public String getCoverageMasterId() { return coverageMasterId; }
    public String getCoverageName() { return coverageName; }
    public CoverageType getCoverageType() { return coverageType; }
    public Money getLimitAmount() { return limitAmount; }
    public boolean isMandatory() { return mandatory; }
}
