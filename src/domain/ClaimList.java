package domain;

import common.exception.domain.NotFoundException;
import domain.common.Money;
import infra.dao.ClaimDao;

import java.util.Collections;
import java.util.List;

public class ClaimList {
    private final ClaimDao dao;
    private final List<Claim> claims;

    public ClaimList(ClaimDao dao) {
        this.dao = dao;
        this.claims = Collections.emptyList();
    }

    public ClaimList(List<Claim> claims) {
        this.dao = null;
        this.claims = Collections.unmodifiableList(claims);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public ClaimList findAwaitingPayment() {
        return dao.findAwaitingPayment();
    }

    public Claim findByAccidentId(String accidentId) {
        if (!claims.isEmpty()) {
            return claims.stream()
                .filter(c -> accidentId.equals(c.getAccidentId()))
                .findFirst()
                .orElse(null);
        }
        return dao.findByAccidentId(accidentId);
    }

    public Claim findById(String claimId) {
        return dao.findById(claimId);
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
        dao.save(claim);
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
