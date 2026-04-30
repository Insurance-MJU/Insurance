package domain;

public class DriverScope {
    private int minAge;
    private ScopeType scopeType;

    public enum ScopeType {}

    public boolean allowsAge(int age) { return age >= minAge; }
    public int getMinAge() { return minAge; }
    public ScopeType getScopeType() { return scopeType; }
    public boolean isRestricted() { return scopeType != null; }
}
