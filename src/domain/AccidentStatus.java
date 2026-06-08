package domain;

public enum AccidentStatus {
    PENDING("접수 완료"),
    INVESTIGATING("현장 조사 중"),
    DAMAGE_ASSESSED("손해 산정 완료"),
    CLOSED("지급 완료"),
    // 하위 호환
    TRANSFERRED("보상팀 이관"),
    IN_PROGRESS("처리중");

    private final String label;
    AccidentStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
