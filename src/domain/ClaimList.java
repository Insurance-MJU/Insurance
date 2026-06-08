package domain;

import common.exception.domain.NotFoundException;
import domain.common.Money;
import infra.dao.ClaimDao;
import infra.vo.ClaimVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimList {
    private final ClaimDao dao;
    private final List<Claim> claims;

    public ClaimList(ClaimDao dao) {
        this.dao    = dao;
        this.claims = Collections.emptyList();
    }

    public ClaimList(List<Claim> claims) {
        this.dao    = null;
        this.claims = Collections.unmodifiableList(claims);
    }

    private static Claim toDomain(ClaimVO vo) {
        if (vo == null) return null;
        Claim c = new Claim();
        c.setClaimId(vo.claimId);
        c.setClaimantName(vo.claimantName);
        c.setClaimDate(vo.claimDate);
        c.setContractId(vo.contractId);
        c.setDescription(vo.description);
        if (vo.claimStatus != null) c.setClaimStatus(ClaimStatus.valueOf(vo.claimStatus));
        c.setAssignedEmployee(vo.assignedEmployee);
        if (vo.accidentId != null) {
            Accident acc = new Accident();
            acc.setAccidentId(vo.accidentId);
            c.setAccident(acc);
        }
        if (vo.settlementAmount > 0 || vo.compensationAmount > 0) {
            if (c.getDamageInvestigation() == null) c.setDamageInvestigation(new DamageInvestigation());
            c.getDamageInvestigation().setAssessment(new DamageAssessment(
                new Money(vo.settlementAmount, "KRW"),
                new Money(vo.deductibleAmount, "KRW"),
                new Money(vo.compensationAmount, "KRW")));
        }
        if (vo.bankName != null && !vo.bankName.isEmpty()) {
            DamageAssessment da = c.getDamageAssessment();
            if (da == null) {
                if (c.getDamageInvestigation() == null) c.setDamageInvestigation(new DamageInvestigation());
                da = new DamageAssessment();
                c.getDamageInvestigation().setAssessment(da);
            }
            da.setClaimPayment(new ClaimPayment(vo.bankName, vo.accountNumber));
        }
        return c;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public ClaimList findAll() {
        return new ClaimList(dao.findAll().stream().map(ClaimList::toDomain).collect(Collectors.toList()));
    }

    public ClaimList findAwaitingPayment() {
        return new ClaimList(dao.findAwaitingPayment().stream().map(ClaimList::toDomain).collect(Collectors.toList()));
    }

    public Claim findByAccidentId(String accidentId) {
        if (!claims.isEmpty()) {
            return claims.stream()
                .filter(c -> accidentId.equals(c.getAccidentId()))
                .findFirst()
                .orElse(null);
        }
        return toDomain(dao.findByAccidentId(accidentId));
    }

    public Claim findById(String claimId) {
        return toDomain(dao.findById(claimId));
    }

    public Claim getById(String claimId) {
        Claim c = findById(claimId);
        if (c == null) throw new NotFoundException("클레임을 찾을 수 없습니다: " + claimId);
        return c;
    }

    public String nextId() {
        return dao.nextId();
    }

    public void save(Claim claim) {
        DamageAssessment da = claim.getDamageAssessment();
        dao.save(new ClaimVO(
            claim.getClaimId(), claim.getClaimantName(), claim.getClaimDate(),
            claim.getContractId(), claim.getDescription(),
            claim.getClaimStatus() != null ? claim.getClaimStatus().name() : null,
            claim.getAssignedEmployee(),
            claim.getAccident() != null ? claim.getAccident().getAccidentId() : null,
            da != null && da.getSettlement()         != null ? da.getSettlement().getAmount()         : 0L,
            da != null && da.getDeductibleAmount()   != null ? da.getDeductibleAmount().getAmount()   : 0L,
            da != null && da.getCompensationAmount() != null ? da.getCompensationAmount().getAmount() : 0L,
            claim.getBankName(), claim.getAccountNumber()
        ));
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Claim> getAll() { return claims; }
    public boolean isEmpty() { return claims.isEmpty(); }
    public int size() { return claims.size(); }

    public Money totalCompensation() {
        long total = claims.stream()
            .filter(c -> c.getCompensationAmount() != null)
            .mapToLong(c -> c.getCompensationAmount().getAmount())
            .sum();
        return new Money(total, "KRW");
    }
}
