package infra.vo;

public class SelectedCoverageVO {
    public final String coverageMasterId;
    public final String coverageName;
    public final boolean mandatory;
    public final String deductibleType;
    public final long deductibleAmount;

    public SelectedCoverageVO(String coverageMasterId, String coverageName,
                              boolean mandatory, String deductibleType, long deductibleAmount) {
        this.coverageMasterId  = coverageMasterId;
        this.coverageName      = coverageName;
        this.mandatory         = mandatory;
        this.deductibleType    = deductibleType;
        this.deductibleAmount  = deductibleAmount;
    }
}
