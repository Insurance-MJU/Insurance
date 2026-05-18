package domain;

public enum ContractStatus {
    ACTIVE, EXPIRED, CANCELLED;

    public String getLabel() {
        switch (this) {
            case ACTIVE:    return "유지중";
            case EXPIRED:   return "만기";
            case CANCELLED: return "해지";
            default:        return "";
        }
    }
}
