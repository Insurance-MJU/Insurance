package infra.dao;

import domain.DamageInvestigation;
import infra.util.FileStore;
import java.util.*;

public class DamageInvestigationDao {
    private static final DamageInvestigationDao INSTANCE = new DamageInvestigationDao();
    public static DamageInvestigationDao getInstance() { return INSTANCE; }

    private static final List<DamageInvestigation> STORE;
    static {
        List<DamageInvestigation> loaded = FileStore.load("investigations.dat");
        STORE = (loaded != null) ? loaded : new ArrayList<>();
    }

    public void save(DamageInvestigation inv) {
        STORE.removeIf(i -> i.getAccidentId().equals(inv.getAccidentId()));
        STORE.add(inv);
        FileStore.save("investigations.dat", STORE);
    }

    public DamageInvestigation findByAccidentId(String accidentId) {
        return STORE.stream().filter(i -> i.getAccidentId().equals(accidentId)).findFirst().orElse(null);
    }
}
