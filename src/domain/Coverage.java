package domain;

import domain.common.Money;
import java.util.List;

public class Coverage {
    private String coverageId;
    private String coverageName;
    private CoverageType coverageType;
    private Deductible deductible;
    private List<Exclusion> exclusions;
    private Money limitAmount;
    private List<CoverageLimitOption> limitOptions;
    private LimitType limitType;
    private LimitUnit limitUnit;
    private boolean mandatory;
    private List<StandardProvisions> provisions;
    private List<Coverage> requiredCoverages;

    // ── DAO 위임 ──────────────────────────────────────────────
    public static List<Coverage> findAll()          { return infra.dao.CoverageDao.getInstance().findAll(); }
    public static Coverage findById(String coverageId)        { return infra.dao.CoverageDao.getInstance().findById(coverageId); }

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
    public void setExclusions(List<Exclusion> v)             { this.exclusions = v; }
    public void setProvisions(List<StandardProvisions> v)   { this.provisions = v; }
    public void setRequiredCoverages(List<Coverage> v)    { this.requiredCoverages = v; }

    // Getters
    public String getCoverageId()                  { return coverageId; }
    public String getCoverageName()                { return coverageName; }
    public CoverageType getCoverageType()          { return coverageType; }
    public Deductible getDeductible()              { return deductible; }
    public List<Exclusion> getExclusions()             { return exclusions; }
    public Money getLimitAmount()                      { return limitAmount; }
    public List<CoverageLimitOption> getLimitOptions() { return limitOptions; }
    public LimitType getLimitType()                    { return limitType; }
    public LimitUnit getLimitUnit()                    { return limitUnit; }
    public List<StandardProvisions> getProvisions()    { return provisions; }
    public List<Coverage> getRequiredCoverages()   { return requiredCoverages; }
}
