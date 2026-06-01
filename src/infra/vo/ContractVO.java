package infra.vo;

import java.util.Date;
import java.util.List;

public class ContractVO {
    public final String contractId;
    public final String policyNo;
    public final String productName;
    public final String subscriptionNo;
    public final long premium;
    public final String carNumber;
    public final String coveragesDescription;
    public final String coverageLimit;
    public final String ridersDescription;
    public final Date issueDate;
    public final Date startDate;
    public final Date endDate;
    public final String status;
    public final String holderName;
    public final String holderPartyId;
    public final List<SelectedCoverageVO> selectedCoverages;

    public ContractVO(String contractId, String policyNo, String productName, String subscriptionNo,
                      long premium, String carNumber, String coveragesDescription, String coverageLimit,
                      String ridersDescription, Date issueDate, Date startDate, Date endDate,
                      String status, String holderName, String holderPartyId,
                      List<SelectedCoverageVO> selectedCoverages) {
        this.contractId           = contractId;
        this.policyNo             = policyNo;
        this.productName          = productName;
        this.subscriptionNo       = subscriptionNo;
        this.premium              = premium;
        this.carNumber            = carNumber;
        this.coveragesDescription = coveragesDescription;
        this.coverageLimit        = coverageLimit;
        this.ridersDescription    = ridersDescription;
        this.issueDate            = issueDate;
        this.startDate            = startDate;
        this.endDate              = endDate;
        this.status               = status;
        this.holderName           = holderName;
        this.holderPartyId        = holderPartyId;
        this.selectedCoverages    = selectedCoverages;
    }
}
