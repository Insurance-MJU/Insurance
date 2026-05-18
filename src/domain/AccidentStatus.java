package domain;

public enum AccidentStatus {
    PENDING("미처리"),
    TRANSFERRED("보상팀 이관"),
    IN_PROGRESS("처리중"),
    CLOSED("지급 종결");

    private final String label;
    AccidentStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
