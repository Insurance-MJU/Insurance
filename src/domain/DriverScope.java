package domain;

import java.io.Serializable;

public class DriverScope implements Serializable {
    private static final long serialVersionUID = 1L;

    private int minAge;
    private ScopeType scopeType;
    private Party familyMember;  // 가족한정 시 등록된 가족 구성원

    public enum ScopeType {
        SELF("본인한정"), FAMILY("가족한정"), ALL("누구나");

        private final String label;
        ScopeType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // ── 비즈니스 메서드 ───────────────────────────────────────
    public void restrictToSelf() {
        this.scopeType = ScopeType.SELF;
        this.familyMember = null;
    }

    public void restrictToFamily(Party familyMember, int minAge) {
        this.scopeType = ScopeType.FAMILY;
        this.familyMember = familyMember;
        this.minAge = minAge;
    }

    public boolean allowsAge(int age)  { return age >= minAge; }
    public boolean isRestricted()      { return scopeType != ScopeType.ALL; }

    public String getScopeLabel() {
        return scopeType != null ? scopeType.getLabel() : "";
    }

    // Getters
    public int getMinAge()          { return minAge; }
    public ScopeType getScopeType() { return scopeType; }
    public Party getFamilyMember()  { return familyMember; }

    // Setters
    public void setMinAge(int v)          { this.minAge = v; }
    public void setScopeType(ScopeType v) { this.scopeType = v; }
}
