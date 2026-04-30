package domain;

public class CoverageLimitDetail {
    private int amount;
    private int detailId;
    private DetailType detailType;
    private int optionId;

    public enum DetailType {}

    public int getAmount() { return amount; }
    public int getDetailId() { return detailId; }
    public DetailType getDetailType() { return detailType; }
    public int getOptionId() { return optionId; }
}
