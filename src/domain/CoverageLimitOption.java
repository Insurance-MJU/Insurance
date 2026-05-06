package domain;

import java.io.Serializable;
import java.util.List;

public class CoverageLimitOption implements Serializable {
    private static final long serialVersionUID = 1L;
    private int coverageMasterId;
    private List<CoverageLimitDetail> details;
    private int optionId;
    private int optionName;

    public List<CoverageLimitDetail> getDetails() { return details; }
    public String getOptionId() { return String.valueOf(optionId); }
    public String getOptionName() { return String.valueOf(optionName); }
}
