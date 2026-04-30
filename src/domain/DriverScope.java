package domain;

public class DriverScope {
    private int minAge;
    private ScopeType scopeType;

    public enum ScopeType { SELF, FAMILY, ALL }

    public boolean allowsAge(int age)  { return age >= minAge; }
    public int getMinAge()             { return minAge; }
    public ScopeType getScopeType()    { return scopeType; }
    public boolean isRestricted()      { return scopeType != ScopeType.ALL; }

    public String getScopeLabel() {
        if (scopeType == ScopeType.SELF)   return "본인한정";
        if (scopeType == ScopeType.FAMILY) return "가족한정";
        if (scopeType == ScopeType.ALL)    return "전가족";
        return "";
    }

    // Setters
    public void setMinAge(int v)          { this.minAge = v; }
    public void setScopeType(ScopeType v) { this.scopeType = v; }
}
