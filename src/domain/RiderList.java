package domain;

import infra.dao.RiderDao;
import infra.vo.RiderVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RiderList {
    private final RiderDao dao;
    private final List<Rider> riders;

    public RiderList(RiderDao dao) {
        this.dao    = dao;
        this.riders = Collections.emptyList();
    }

    public RiderList(List<Rider> riders) {
        this.dao    = null;
        this.riders = Collections.unmodifiableList(riders);
    }

    private static Rider toDomain(RiderVO vo) {
        if (vo == null) return null;
        Rider r = new Rider();
        r.setRiderId(vo.riderId);
        r.setRiderCode(vo.riderCode);
        r.setRiderName(vo.riderName);
        r.setDescription(vo.description);
        if (vo.riderType != null) {
            try { r.setRiderType(RiderType.valueOf(vo.riderType)); } catch (Exception ignored) {}
        }
        r.setMandatory(vo.mandatory);
        r.setDiscountRate(vo.discountRate);
        return r;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public RiderList findAll() {
        return new RiderList(dao.findAll().stream().map(RiderList::toDomain).collect(Collectors.toList()));
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
        return toDomain(dao.findByCode(riderCode));
    }

    public Rider findById(String riderId) {
        return riders.stream()
            .filter(r -> riderId.equals(r.getRiderId()))
            .findFirst()
            .orElse(null);
    }
}
