package domain;

public enum SubscriptionStatus {
    PENDING_REVIEW, APPROVED, REJECTED, SUPPLEMENT_REQUIRED;

    public String getLabel() {
        switch (this) {
            case PENDING_REVIEW:      return "심사대기중";
            case APPROVED:            return "인수승인";
            case REJECTED:            return "인수거절";
            case SUPPLEMENT_REQUIRED: return "서류보완요청";
            default:                  return "";
        }
    }
}
