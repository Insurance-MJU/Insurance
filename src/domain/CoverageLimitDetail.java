package domain;

import java.io.Serializable;

public class CoverageLimitDetail implements Serializable {
    private static final long serialVersionUID = 1L;
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
