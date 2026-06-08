package infra.vo;

public class CoverageLimitOptionVO {
    public final String coverageMasterId;
    public final int optionId;
    public final String optionName;

    public CoverageLimitOptionVO(String coverageMasterId, int optionId, String optionName) {
        this.coverageMasterId = coverageMasterId;
        this.optionId         = optionId;
        this.optionName       = optionName;
    }
}
