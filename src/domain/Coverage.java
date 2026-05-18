package domain;

import domain.common.Money;

import java.util.Arrays;
import java.util.Collections;
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

    // ── 마스터 카탈로그 ───────────────────────────────────────
    private static final List<Coverage> CATALOG;

    static {
        CATALOG = Arrays.asList(
            build("COV-001", "대인배상 I",   CoverageType.PERSONAL_INJURY_MANDATORY, true,
                Collections.singletonList(option("OPT-001-1", "COV-001", "기본 옵션"))),
            build("COV-002", "대인배상 II",  CoverageType.PERSONAL_INJURY_OPTIONAL, false,
                Arrays.asList(option("OPT-002-1", "COV-002", "한도5억"), option("OPT-002-2", "COV-002", "무한"))),
            build("COV-003", "대물배상",     CoverageType.PROPERTY_DAMAGE, false,
                Collections.singletonList(option("OPT-003-1", "COV-003", "기본옵션"))),
            build("COV-004", "자동차상해",   CoverageType.AUTO_INJURY, false,
                Collections.singletonList(option("OPT-004-1", "COV-004", "기본옵션"))),
            build("COV-005", "자기차량손해", CoverageType.OWN_VEHICLE_DAMAGE, false,
                Collections.singletonList(option("OPT-005-1", "COV-005", "기본옵션"))),
            build("COV-006", "무보험차상해", CoverageType.UNINSURED_VEHICLE, false,
                Collections.singletonList(option("OPT-006-1", "COV-006", "기본옵션")))
        );
    }

    public static List<Coverage> catalog() {
        return Collections.unmodifiableList(CATALOG);
    }

    public static boolean hasMandatoryCoverage(List<Coverage> selected) {
        return selected.stream()
            .anyMatch(c -> c.coverageType == CoverageType.PERSONAL_INJURY_MANDATORY);
    }

    private static Coverage build(String id, String name, CoverageType type, boolean mandatory,
                                   List<CoverageLimitOption> options) {
        Coverage c = new Coverage();
        c.coverageId   = id;
        c.coverageName = name;
        c.coverageType = type;
        c.mandatory    = mandatory;
        c.limitOptions = options;
        return c;
    }

    private static CoverageLimitOption option(String optId, String covId, String name) {
        CoverageLimitOption opt = new CoverageLimitOption();
        opt.setCoverageMasterId(covId);
        opt.setOptionName(name);
        return opt;
    }

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
