package infra.dao;

import domain.*;
import domain.common.Money;
import infra.util.FileStore;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimDao {
    private static final ClaimDao INSTANCE = new ClaimDao();
    public static ClaimDao getInstance() { return INSTANCE; }

    private static final List<Claim> STORE;
    static {
        List<Claim> loaded = FileStore.load("claims.dat");
        if (loaded != null) { STORE = loaded; }
        else { STORE = new ArrayList<>(); initDefaults(); }
    }

    private static void initDefaults() {
        Accident accident = AccidentDao.getInstance().findById("ACC-2026-003");
        Claim c = new Claim("CL-00001", accident, "이영희", "2026-04-18",
                            "CNT-20231210-003", "차량 전손", ClaimStatus.PAYMENT_PENDING);
        c.setAssignedEmployee("EMP-1023");
        Money settlement = new Money(14_800_000, "KRW");
        c.setDamageAssessment(new DamageAssessment(settlement, Deductible.none(), new Money(14_800_000, "KRW")));
        STORE.add(c);
        FileStore.save("claims.dat", STORE);
    }

    public Claim findByAccidentId(String accidentId) {
        return STORE.stream()
            .filter(c -> c.getAccident() != null && accidentId.equals(c.getAccident().getAccidentId()))
            .findFirst().orElse(null);
    }

    public Claim findById(String claimId) {
        return STORE.stream().filter(c -> c.getClaimId().equals(claimId)).findFirst().orElse(null);
    }

    public List<Claim> findAwaitingPayment() {
        return STORE.stream()
            .filter(c -> c.getClaimStatus() == ClaimStatus.PAYMENT_PENDING)
            .collect(Collectors.toList());
    }

    public void save(Claim c) {
        STORE.removeIf(x -> x.getClaimId().equals(c.getClaimId()));
        STORE.add(c);
        FileStore.save("claims.dat", STORE);
    }

    public String nextId() {
        return String.format("CL-%05d", STORE.size() + 1);
    }
}
