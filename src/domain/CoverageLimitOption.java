package domain;

import java.util.List;

public class CoverageLimitOption {
    private int coverageMasterId;
    private List<CoverageLimitDetail> details;
    private int optionId;
    private int optionName;

    public List<CoverageLimitDetail> getDetails() { return details; }
    public String getOptionId() { return String.valueOf(optionId); }
    public String getOptionName() { return String.valueOf(optionName); }
}
