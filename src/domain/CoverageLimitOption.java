package domain;

import java.io.Serializable;
import java.util.List;

public class CoverageLimitOption implements Serializable {
    private static final long serialVersionUID = 1L;
    private String coverageMasterId;
    private List<CoverageLimitDetail> details;
    private int optionId;
    private String optionName;

    // Setters
    public void setCoverageMasterId(String v)          { this.coverageMasterId = v; }
    public void setOptionId(int v)                     { this.optionId = v; }
    public void setOptionName(String v)                { this.optionName = v; }
    public void setDetails(List<CoverageLimitDetail> v){ this.details = v; }

    // Getters
    public String getCoverageMasterId()        { return coverageMasterId; }
    public List<CoverageLimitDetail> getDetails() { return details; }
    public int getOptionId()                   { return optionId; }
    public String getOptionName()              { return optionName; }
}
