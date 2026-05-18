package domain;

import domain.common.Money;
import domain.exception.ValidationException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private ContractStatus status;
    private String carNumber;
    private String coveragesDescription;
    private String coverageLimit;
    private String ridersDescription;
    private String productName;
    private String subscriptionNo;

    // ── 정적 팩토리: 신규 계약 발행 ──────────────────────────
    public static Contract issue(String policyNo, String contractId, String productName,
                                  Party holder, Money premium, String carNumber,
                                  String coveragesDescription, String coverageLimit,
                                  String ridersDescription) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (policyNo == null || policyNo.isBlank())       errors.add("증권번호는 필수입니다");
        if (contractId == null || contractId.isBlank())   errors.add("계약ID는 필수입니다");
        if (productName == null || productName.isBlank()) errors.add("상품명은 필수입니다");
        if (holder == null)                               errors.add("계약자 정보는 필수입니다");
        if (premium == null || premium.getAmount() <= 0)  errors.add("보험료는 0보다 커야 합니다");
        if (carNumber == null || carNumber.isBlank())     errors.add("차량번호는 필수입니다");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        Contract c = new Contract();
        c.policyNo             = policyNo;
        c.contractId           = contractId;
        c.productName          = productName;
        c.policyholder         = holder;
        c.premium              = premium;
        c.carNumber            = carNumber;
        c.coveragesDescription = coveragesDescription;
        c.coverageLimit        = coverageLimit;
        c.ridersDescription    = ridersDescription;
        c.issueDate            = new java.util.Date();
        c.startDate            = new java.util.Date();
        c.activate();
        return c;
    }

    public void activate()   { this.status = ContractStatus.ACTIVE; }
    public void cancel()     { this.status = ContractStatus.CANCELLED; }
    public void mature()     { this.status = ContractStatus.EXPIRED; }
    public void reinstate()  { this.status = ContractStatus.ACTIVE; }
    public boolean isActive() { return this.status == ContractStatus.ACTIVE; }

    public String getStatusLabel() {
        return status != null ? status.getLabel() : "";
    }

    public String getIssueDateString() {
        if (issueDate == null) return "-";
        return new SimpleDateFormat("yyyy-MM-dd").format(issueDate);
    }

    public List<Party> getBeneficiaries()         { return beneficiaries; }
    public String getContractId()                 { return contractId; }
    public Date getEndDate()                      { return endDate; }
    public Insured getInsured()                   { return insured; }
    public Date getIssueDate()                    { return issueDate; }
    public Party getNamedInsured()                { return namedInsured; }
    public Party getPolicyholder()                { return policyholder; }
    public String getPolicyNo()                   { return policyNo; }
    public Money getPremium()                     { return premium; }
    public Product getProduct()                   { return product; }
    public String getProductId()                  { return productId; }
    public List<SelectedCoverage> getSelectedCoverages() { return selectedCoverages; }
    public List<SelectedRider> getSelectedRiders()       { return selectedRiders; }
    public Date getStartDate()                    { return startDate; }
    public ContractStatus getStatus()             { return status; }
    public String getCarNumber()                  { return carNumber; }
    public String getCoveragesDescription()       { return coveragesDescription; }
    public String getCoverageLimit()              { return coverageLimit != null ? coverageLimit : ""; }
    public String getRidersDescription()          { return ridersDescription; }
    public String getProductName()                { return productName; }

    // ── DAO 위임 ──────────────────────────────────────────────
    public static java.util.List<Contract> findAll()              { return infra.dao.ContractDao.getInstance().findAll(); }
    public static Contract findByPolicyNo(String policyNo)        { return infra.dao.ContractDao.getInstance().findByPolicyNo(policyNo); }
    public static Contract findByContractId(String contractId)    { return infra.dao.ContractDao.getInstance().findByContractId(contractId); }
    public static java.util.List<Contract> findByCondition(String holderName, String periodChoice, String statusChoice) {
        return infra.dao.ContractDao.getInstance().findByCondition(holderName, periodChoice, statusChoice);
    }
    public void save()                                       { infra.dao.ContractDao.getInstance().save(this); }
    public static String nextPolicyNo()                      { return infra.dao.ContractDao.getInstance().nextPolicyNo(); }
    public static String nextContractId()                    { return infra.dao.ContractDao.getInstance().nextContractId(); }

    public void setBeneficiaries(List<Party> v)           { this.beneficiaries = v; }
    public void setContractId(String v)                   { this.contractId = v; }
    public void setEndDate(Date v)                        { this.endDate = v; }
    public void setInsured(Insured v)                     { this.insured = v; }
    public void setIssueDate(Date v)                      { this.issueDate = v; }
    public void setNamedInsured(Party v)                  { this.namedInsured = v; }
    public void setPolicyholder(Party v)                  { this.policyholder = v; }
    public void setPolicyNo(String v)                     { this.policyNo = v; }
    public void setPremium(Money v)                       { this.premium = v; }
    public void setProduct(Product v)                     { this.product = v; }
    public void setProductId(String v)                    { this.productId = v; }
    public void setSelectedCoverages(List<SelectedCoverage> v) { this.selectedCoverages = v; }
    public void setSelectedRiders(List<SelectedRider> v)       { this.selectedRiders = v; }
    public void setStartDate(Date v)                      { this.startDate = v; }
    public void setStatus(ContractStatus v)               { this.status = v; }
    public void setCarNumber(String v)                    { this.carNumber = v; }
    public void setCoveragesDescription(String v)         { this.coveragesDescription = v; }
    public void setCoverageLimit(String v)                { this.coverageLimit = v; }
    public void setRidersDescription(String v)            { this.ridersDescription = v; }
    public void setProductName(String v)                  { this.productName = v; }
    public void setSubscriptionNo(String v)               { this.subscriptionNo = v; }

    public String getSubscriptionNo()                     { return subscriptionNo; }
}
