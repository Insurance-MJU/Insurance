package domain;

import infra.dao.RiderDao;

import java.util.Collections;
import java.util.List;

public class RiderList {
    private final RiderDao dao;
    private final List<Rider> riders;

    public RiderList(RiderDao dao) {
        this.dao = dao;
        this.riders = Collections.emptyList();
    }

    public RiderList(List<Rider> riders) {
        this.dao = null;
        this.riders = Collections.unmodifiableList(riders);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public RiderList findAll() {
        return dao.findAll();
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Rider> getAll() { return riders; }
    public boolean isEmpty() { return riders.isEmpty(); }
    public int size() { return riders.size(); }
    public Rider get(int index) { return riders.get(index); }

    public Rider findByCode(String riderCode) {
        if (!riders.isEmpty()) {
            return riders.stream()
                .filter(r -> riderCode.equals(r.getRiderCode()))
                .findFirst()
                .orElse(null);
        }
        return dao.findByCode(riderCode);
    }

    public Rider findById(String riderId) {
        return riders.stream()
            .filter(r -> riderId.equals(r.getRiderId()))
            .findFirst()
            .orElse(null);
    }
}
