package infra.repository;

import domain.Claim;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimRepository {

    private static final List<Claim> STORE = new ArrayList<>();

    static {
        Claim c = new Claim(
            "CL-00001", "ACC-2026-003",
            "이영희", "2026-04-18",
            "CNT-20231210-003",
            "차량 전손", "지급대기"
        );
        c.setAssignedEmployee("EMP-1023");
        c.setSettlement(1480);
        c.setDeductible(0);
        c.setCompensationAmount(1480);
        STORE.add(c);
    }

    public static Claim findByAccidentId(String accidentId) {
        return STORE.stream()
            .filter(c -> c.getAccidentId().equals(accidentId))
            .findFirst().orElse(null);
    }

    public static Claim findById(String claimId) {
        return STORE.stream()
            .filter(c -> c.getClaimId().equals(claimId))
            .findFirst().orElse(null);
    }

    public static List<Claim> findAwaitingPayment() {
        return STORE.stream()
            .filter(c -> "지급대기".equals(c.getStatus()))
            .collect(Collectors.toList());
    }

    public static void save(Claim claim) {
        STORE.removeIf(c -> c.getClaimId().equals(claim.getClaimId()));
        STORE.add(claim);
    }

    public static void updateStatus(String claimId, String status) {
        STORE.stream()
            .filter(c -> c.getClaimId().equals(claimId))
            .findFirst()
            .ifPresent(c -> c.setStatus(status));
    }

    public static String nextId() {
        return String.format("CL-%05d", STORE.size() + 1);
    }
}
