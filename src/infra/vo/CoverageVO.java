package infra.vo;

import java.util.List;

public class CoverageVO {
    public final String coverageId;
    public final String coverageName;
    public final boolean mandatory;
    public final String coverageType;
    public final List<CoverageLimitOptionVO> limitOptions;

    public CoverageVO(String coverageId, String coverageName, boolean mandatory,
                      String coverageType, List<CoverageLimitOptionVO> limitOptions) {
        this.coverageId   = coverageId;
        this.coverageName = coverageName;
        this.mandatory    = mandatory;
        this.coverageType = coverageType;
        this.limitOptions = limitOptions;
    }
}
