package domain;

import domain.common.Money;

import java.util.List;

public class Coverage {
    private String coverageId;
    private String coverageName;
    private CoverageType coverageType;
    private Deductible deductible;
    private List<String> exclusions;
    private Money limitAmount;
    private List<CoverageLimitOption> limitOptions;
    private LimitType limitType;
    private LimitUnit limitUnit;
    private boolean mandatory;
    private List<String> provisions;
    private List<Coverage> requiredCoverages;

    public enum CoverageType {}
    public enum LimitType {}
    public enum LimitUnit {}

    public boolean hasDeductible() {
        return deductible != null;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public boolean isUnlimited() {
        return limitAmount == null && limitType == null;
    }

    public boolean requiresCoverage(CoverageType type) {
        if (requiredCoverages == null) return false;
        return requiredCoverages.stream()
                .anyMatch(c -> c.coverageType == type);
    }

    public String getCoverageId() { return coverageId; }
    public String getCoverageName() { return coverageName; }
    public CoverageType getCoverageType() { return coverageType; }
    public Deductible getDeductible() { return deductible; }
    public List<String> getExclusions() { return exclusions; }
    public Money getLimitAmount() { return limitAmount; }
    public List<CoverageLimitOption> getLimitOptions() { return limitOptions; }
    public LimitType getLimitType() { return limitType; }
    public LimitUnit getLimitUnit() { return limitUnit; }
    public List<String> getProvisions() { return provisions; }
    public List<Coverage> getRequiredCoverages() { return requiredCoverages; }
}
