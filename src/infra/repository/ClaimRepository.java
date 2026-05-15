package infra.repository;

import domain.Accident;
import domain.Claim;
import domain.Deductible;
import domain.DamageAssessment;
import domain.common.Money;
import infra.util.FileStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimRepository {

    private static final List<Claim> STORE;

    static {
        List<Claim> loaded = FileStore.load("claims.dat");
        if (loaded != null) {
            STORE = loaded;
        } else {
            STORE = new ArrayList<>();
            initDefaults();
        }
    }

    private static void initDefaults() {
        Accident accident = Accident.findById("ACC-2026-003");
        Claim c = new Claim(
            "CL-00001", accident,
            "이영희", "2026-04-18",
            "CNT-20231210-003",
            "차량 전손", Claim.ClaimStatus.PAYMENT_PENDING
        );
        c.setAssignedEmployee("EMP-1023");
        Money settlement = new Money(14_800_000, "KRW");
        c.setDamageAssessment(new DamageAssessment(settlement, Deductible.none(), new Money(14_800_000, "KRW")));
        STORE.add(c);

        FileStore.save("claims.dat", STORE);
    }

    public static Claim findByAccidentId(String accidentId) {
        return STORE.stream()
            .filter(c -> accidentId.equals(c.getAccidentId()))
            .findFirst().orElse(null);
    }

    public static Claim findById(String claimId) {
        return STORE.stream()
            .filter(c -> c.getClaimId().equals(claimId))
            .findFirst().orElse(null);
    }

    public static List<Claim> findAwaitingPayment() {
        return STORE.stream()
            .filter(c -> c.getClaimStatus() == Claim.ClaimStatus.PAYMENT_PENDING)
            .collect(Collectors.toList());
    }

    public static void save(Claim claim) {
        STORE.removeIf(c -> c.getClaimId().equals(claim.getClaimId()));
        STORE.add(claim);
        FileStore.save("claims.dat", STORE);
    }

    public static void updateStatus(String claimId, Claim.ClaimStatus status) {
        STORE.stream()
            .filter(c -> c.getClaimId().equals(claimId))
            .findFirst()
            .ifPresent(c -> c.setClaimStatus(status));
        FileStore.save("claims.dat", STORE);
    }

    public static String nextId() {
        return String.format("CL-%05d", STORE.size() + 1);
    }
}
