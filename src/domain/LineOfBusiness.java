package domain;

public enum LineOfBusiness {
    AUTO("자동차보험"), LIFE("생명보험"), FIRE("화재보험");
    private final String label;
    LineOfBusiness(String label) { this.label = label; }
    public String getLabel() { return label; }
}
