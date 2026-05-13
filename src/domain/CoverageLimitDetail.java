package domain;

import java.io.Serializable;

public class CoverageLimitDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private int amount;
    private int detailId;
    private DetailType detailType;
    private int optionId;

    public enum DetailType {
        PER_PERSON,  // 1인당 한도 (대인배상)
        PER_ACCIDENT, // 사고당 한도
        AGGREGATE,   // 총 보상 한도
        DEDUCTIBLE   // 자기부담금 기준
    }

    public int getAmount() { return amount; }
    public int getDetailId() { return detailId; }
    public DetailType getDetailType() { return detailType; }
    public int getOptionId() { return optionId; }
}
