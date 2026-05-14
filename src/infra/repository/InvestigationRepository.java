package infra.repository;

import domain.claim.DamageInvestigation;
import infra.util.FileStore;

import java.util.ArrayList;
import java.util.List;

public class InvestigationRepository {

    private static final List<DamageInvestigation> STORE;

    static {
        List<DamageInvestigation> loaded = FileStore.load("investigations.dat");
        STORE = (loaded != null) ? loaded : new ArrayList<>();
    }

    public static void save(DamageInvestigation inv) {
        STORE.removeIf(i -> i.getAccidentId().equals(inv.getAccidentId()));
        STORE.add(inv);
        FileStore.save("investigations.dat", STORE);
    }

    public static DamageInvestigation findByAccidentId(String accidentId) {
        return STORE.stream()
            .filter(i -> i.getAccidentId().equals(accidentId))
            .findFirst().orElse(null);
    }
}
