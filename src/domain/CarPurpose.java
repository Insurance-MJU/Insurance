package domain;

public enum CarPurpose {
    COMMUTE("출퇴근/가정용", 1.0),
    BUSINESS("업무용",      1.1),
    COMMERCIAL("영업용",    1.3);

    private final String label;
    private final double multiplier;

    CarPurpose(String label, double multiplier) {
        this.label      = label;
        this.multiplier = multiplier;
    }

    public String getLabel()      { return label; }
    public double getMultiplier() { return multiplier; }
}
