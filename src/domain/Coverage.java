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

    public enum CoverageType {
        PERSONAL_INJURY_MANDATORY,  // 대인배상 I (의무)
        PERSONAL_INJURY_OPTIONAL,   // 대인배상 II
        PROPERTY_DAMAGE,            // 대물배상
        AUTO_INJURY,                // 자동차상해
        OWN_VEHICLE_DAMAGE,         // 자기차량손해
        UNINSURED_VEHICLE           // 무보험차상해
    }

    public enum LimitType { FIXED, UNLIMITED, PER_OCCURRENCE }
    public enum LimitUnit  { KRW, PERCENT }

    public boolean hasDeductible() { return deductible != null; }
    public boolean isMandatory()   { return mandatory; }
    public boolean isUnlimited()   { return limitAmount == null && limitType == null; }

    public boolean requiresCoverage(CoverageType type) {
        if (requiredCoverages == null) return false;
        return requiredCoverages.stream().anyMatch(c -> c.coverageType == type);
    }

    // Setters
    public void setCoverageId(String v)                   { this.coverageId = v; }
    public void setCoverageName(String v)                 { this.coverageName = v; }
    public void setCoverageType(CoverageType v)           { this.coverageType = v; }
    public void setMandatory(boolean v)                   { this.mandatory = v; }
    public void setLimitOptions(List<CoverageLimitOption> v) { this.limitOptions = v; }
    public void setDeductible(Deductible v)               { this.deductible = v; }
    public void setLimitAmount(Money v)                   { this.limitAmount = v; }
    public void setLimitType(LimitType v)                 { this.limitType = v; }
    public void setLimitUnit(LimitUnit v)                 { this.limitUnit = v; }
    public void setExclusions(List<String> v)             { this.exclusions = v; }
    public void setProvisions(List<String> v)             { this.provisions = v; }
    public void setRequiredCoverages(List<Coverage> v)    { this.requiredCoverages = v; }

    // Getters
    public String getCoverageId()                  { return coverageId; }
    public String getCoverageName()                { return coverageName; }
    public CoverageType getCoverageType()          { return coverageType; }
    public Deductible getDeductible()              { return deductible; }
    public List<String> getExclusions()            { return exclusions; }
    public Money getLimitAmount()                  { return limitAmount; }
    public List<CoverageLimitOption> getLimitOptions() { return limitOptions; }
    public LimitType getLimitType()                { return limitType; }
    public LimitUnit getLimitUnit()                { return limitUnit; }
    public List<String> getProvisions()            { return provisions; }
    public List<Coverage> getRequiredCoverages()   { return requiredCoverages; }
}
