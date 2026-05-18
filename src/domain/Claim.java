package domain;

import domain.common.Money;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Claim implements Serializable {
    private static final long serialVersionUID = 1L;
    private String claimId;
    private Accident accident;
    private String claimantName;
    private Date claimDate;
    private String contractId;
    private String description;
    private ClaimStatus claimStatus;
    private ClaimType claimType;
    private String assignedEmployee;
    private DamageInvestigation damageInvestigation;

    public Claim() {}

    public Claim(String claimId, Accident accident, String claimantName, String claimDate,
                 String contractId, String description, ClaimStatus claimStatus) {
        this.claimId = claimId;
        this.accident = accident;
        this.claimantName = claimantName;
        try { this.claimDate = new SimpleDateFormat("yyyy-MM-dd").parse(claimDate); } catch (Exception e) { this.claimDate = new Date(); }
        this.contractId = contractId;
        this.description = description;
        this.claimStatus = claimStatus;
    }

    public String createClaim() { return claimId; }
    public String getClaimInfo() { return null; }
    public boolean updateStatus(ClaimStatus s) { this.claimStatus = s; return true; }

    /** 손해액 산정 완료: 합의금·자기부담금으로 보상금 계산 후 지급대기 상태로 전환 */
    public void assess(Money settlement, Money deductibleAmount) {
        long dedAmt = (deductibleAmount != null) ? deductibleAmount.getAmount() : 0L;
        Money compensation = new Money(settlement.getAmount() - dedAmt, "KRW");
        if (this.damageInvestigation == null) this.damageInvestigation = new DamageInvestigation();
        this.damageInvestigation.setAssessment(new DamageAssessment(settlement, deductibleAmount, compensation));
        this.claimStatus = ClaimStatus.PAYMENT_PENDING;
    }

    /** 보험금 지급 완료: DamageAssessment에 지급 정보 저장 후 지급완료 상태로 전환 */
    public void completePayment(String bank, String accountNo) {
        if (this.damageInvestigation == null) this.damageInvestigation = new DamageInvestigation();
        DamageAssessment da = this.damageInvestigation.getAssessment();
        if (da == null) { da = new DamageAssessment(); this.damageInvestigation.setAssessment(da); }
        da.completePayment(bank, accountNo);
        this.claimStatus = ClaimStatus.CLOSED;
    }

    /** 계좌번호 숫자 자릿수가 14자리 이하인지 검증 */
    public static boolean isValidAccountNumber(String accountNo) {
        return accountNo.replaceAll("[^0-9]", "").length() <= 14;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getClaimId() { return claimId; }
    public Accident getAccident() { return accident; }
    public String getAccidentId() { return accident != null ? accident.getAccidentId() : null; }
    public String getClaimantName() { return claimantName; }
    public Date getClaimDate() { return claimDate; }
    public String getClaimDateDisplay() { return claimDate != null ? new SimpleDateFormat("yyyy-MM-dd").format(claimDate) : ""; }
    public String getContractId() { return contractId; }
    public String getDescription() { return description; }
    public ClaimStatus getClaimStatus() { return claimStatus; }
    public ClaimType getClaimType() { return claimType; }
    public String getAssignedEmployee() { return assignedEmployee; }
    public DamageInvestigation getDamageInvestigation() { return damageInvestigation; }
    public DamageAssessment getDamageAssessment() { return damageInvestigation != null ? damageInvestigation.getAssessment() : null; }
    public ClaimPayment getClaimPayment() { DamageAssessment da = getDamageAssessment(); return da != null ? da.getClaimPayment() : null; }
    public Money getSettlement() { DamageAssessment da = getDamageAssessment(); return da != null ? da.getSettlement() : null; }
    public Money getCompensationAmount() { DamageAssessment da = getDamageAssessment(); return da != null ? da.getCompensationAmount() : null; }
    public String getBankName() { ClaimPayment cp = getClaimPayment(); return cp != null ? cp.getBankName() : null; }
    public String getAccountNumber() { ClaimPayment cp = getClaimPayment(); return cp != null ? cp.getAccountNumber() : null; }

    // ── DAO 위임 ──────────────────────────────────────────────
    public static Claim findByAccidentId(String accidentId)  { return infra.dao.ClaimDao.getInstance().findByAccidentId(accidentId); }
    public static Claim findById(String claimId)             { return infra.dao.ClaimDao.getInstance().findById(claimId); }
    public static java.util.List<Claim> findAwaitingPayment(){ return infra.dao.ClaimDao.getInstance().findAwaitingPayment(); }
    public static String nextId()                            { return infra.dao.ClaimDao.getInstance().nextId(); }
    public void save()                                       { infra.dao.ClaimDao.getInstance().save(this); }

    // ── Setters ──────────────────────────────────────────────
    public void setClaimId(String v) { this.claimId = v; }
    public void setAccident(Accident v) { this.accident = v; }
    public void setClaimantName(String v) { this.claimantName = v; }
    public void setClaimDate(Date v) { this.claimDate = v; }
    public void setContractId(String v) { this.contractId = v; }
    public void setDescription(String v) { this.description = v; }
    public void setClaimStatus(ClaimStatus v) { this.claimStatus = v; }
    public void setClaimType(ClaimType v) { this.claimType = v; }
    public void setAssignedEmployee(String v) { this.assignedEmployee = v; }
    public void setDamageInvestigation(DamageInvestigation v) { this.damageInvestigation = v; }
}
