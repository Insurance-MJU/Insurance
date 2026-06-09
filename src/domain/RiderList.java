package domain;

import infra.dao.RiderDao;
import infra.vo.RiderVO;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, Object> findByCodeAsMap(String riderCode) {
        return toMap(findByCode(riderCode));
    }

    public void saveNew(Rider rider) {
        dao.saveNew(toRequestMap(rider));
    }

    public void updateByCode(String riderCode, Rider rider) {
        dao.updateByCode(riderCode, toRequestMap(rider));
    }

    public void deleteByCode(String riderCode) {
        dao.deleteByCode(riderCode);
    }

    public static Rider fromMap(Map<?, ?> data) {
        Rider r = new Rider();
        r.setRiderCode(str(data.get("riderCode")));
        r.setRiderName(data.get("riderName") != null ? str(data.get("riderName")) : str(data.get("name")));
        r.setDescription(str(data.get("description")));
        if (data.get("riderType") != null) {
            try { r.setRiderType(RiderType.valueOf(str(data.get("riderType")))); } catch (Exception ignored) {}
        }
        r.setMandatory(Boolean.TRUE.equals(data.get("mandatory")));
        r.setDiscountRate(data.get("discountRate") instanceof Number n ? n.doubleValue() : 0.0);
        return r;
    }

    public static Map<String, Object> toMap(Rider r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",           r.getRiderCode());
        m.put("riderCode",    r.getRiderCode());
        m.put("name",         r.getRiderName());
        m.put("riderName",    r.getRiderName());
        m.put("description",  r.getDescription() != null ? r.getDescription() : "");
        m.put("riderType",    r.getRiderType() != null ? r.getRiderType().name() : "DISCOUNT");
        m.put("mandatory",    r.isMandatory());
        m.put("discountRate", r.getDiscountRate());
        m.put("exclusions",   List.of());
        m.put("provisionId",  null);
        return m;
    }

    private static Map<String, Object> toRequestMap(Rider r) {
        return toMap(r);
    }

    private static String str(Object v) {
        return v != null ? v.toString() : null;
    }
}
