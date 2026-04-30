package domain;

import domain.common.Money;

import java.util.Date;
import java.util.List;

public class Contract {
    private List<Party> beneficiaries;
    private String contractId;
    private Date endDate;
    private Insured insured;
    private Date issueDate;
    private Party namedInsured;
    private Party policyholder;
    private String policyNo;
    private Money premium;
    private Product product;
    private String productId;
    private List<SelectedCoverage> selectedCoverages;
    private List<SelectedRider> selectedRiders;
    private Date startDate;
    private Status status;

    public enum Status {}

    public void activate() {}
    public void cancel() {}
    public boolean isActive() { return false; }
    public void mature() {}
    public void reinstate() {}

    public List<Party> getBeneficiaries() { return beneficiaries; }
    public String getContractId() { return contractId; }
    public Date getEndDate() { return endDate; }
    public Insured getInsured() { return insured; }
    public Date getIssueDate() { return issueDate; }
    public Party getNamedInsured() { return namedInsured; }
    public Party getPolicyholder() { return policyholder; }
    public String getPolicyNo() { return policyNo; }
    public Money getPremium() { return premium; }
    public Product getProduct() { return product; }
    public String getProductId() { return productId; }
    public List<SelectedCoverage> getSelectedCoverages() { return selectedCoverages; }
    public List<SelectedRider> getSelectedRiders() { return selectedRiders; }
    public Date getStartDate() { return startDate; }
    public Status getStatus() { return status; }
}
