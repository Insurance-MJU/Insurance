package infra.repository;

import domain.DamageInvestigation;

import java.util.ArrayList;
import java.util.List;

public class InvestigationRepository {

    private static final List<DamageInvestigation> STORE = new ArrayList<>();

    public static void save(DamageInvestigation inv) {
        STORE.removeIf(i -> i.getAccidentId().equals(inv.getAccidentId()));
        STORE.add(inv);
    }

    public static DamageInvestigation findByAccidentId(String accidentId) {
        return STORE.stream()
            .filter(i -> i.getAccidentId().equals(accidentId))
            .findFirst().orElse(null);
    }
}
