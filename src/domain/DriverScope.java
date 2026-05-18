package domain;

public class DriverScope {
    private int minAge;
    private ScopeType scopeType;
    private String familyMemberInfo;

    public enum ScopeType {
        SELF("본인한정"), FAMILY("가족한정"), ALL("누구나");

        private final String label;
        ScopeType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // ── 비즈니스 메서드 ───────────────────────────────────────
    public void restrictToSelf() {
        this.scopeType = ScopeType.SELF;
        this.familyMemberInfo = null;
    }

    public void restrictToFamily(String memberInfo) {
        this.scopeType = ScopeType.FAMILY;
        this.familyMemberInfo = memberInfo;
    }

    public boolean allowsAge(int age)      { return age >= minAge; }
    public boolean isRestricted()          { return scopeType != ScopeType.ALL; }

    public String getScopeLabel() {
        return scopeType != null ? scopeType.getLabel() : "";
    }

    // Getters
    public int getMinAge()              { return minAge; }
    public ScopeType getScopeType()     { return scopeType; }
    public String getFamilyMemberInfo() { return familyMemberInfo; }

    // Setters (레포지토리 초기화 전용)
    public void setMinAge(int v)          { this.minAge = v; }
    public void setScopeType(ScopeType v) { this.scopeType = v; }
}
