package domain;

import java.io.Serializable;

public class DriverScope implements Serializable {
    private static final long serialVersionUID = 1L;

    private int minAge;
    private ScopeType scopeType;
    private Party familyMember;
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
        this.familyMember = null;
    }

    public void restrictToFamily(Party familyMember, int minAge) {
        this.scopeType = ScopeType.FAMILY;
        this.familyMember = familyMember;
        this.minAge = minAge;
    }

    /** CS01처럼 "이름 / 관계 / 생년월일" 문자열로 가족 정보를 등록할 때 사용 */
    public void restrictToFamily(String info) {
        this.scopeType = ScopeType.FAMILY;
        this.familyMemberInfo = info;
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
    public String getFamilyMemberInfo() {
        if (familyMemberInfo != null) return familyMemberInfo;
        if (familyMember != null) return familyMember.getName();
        return "";
    }

    // Setters
    public void setMinAge(int v)          { this.minAge = v; }
    public void setScopeType(ScopeType v) { this.scopeType = v; }
}
