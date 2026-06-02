package domain;

public enum ExternalReviewResult {
    APPROVED("승인"),
    REJECTED("거절"),
    SUPPLEMENT_REQUIRED("보완요청");

    private final String label;

    ExternalReviewResult(String label) { this.label = label; }

    public String getLabel() { return label; }
}
