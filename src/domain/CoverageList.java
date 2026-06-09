package domain;

import infra.dao.CoverageDao;
import infra.vo.CoverageLimitOptionVO;
import infra.vo.CoverageVO;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CoverageList {
    private final CoverageDao dao;
    private final List<Coverage> coverages;

    public CoverageList(CoverageDao dao) {
        this.dao       = dao;
        this.coverages = Collections.emptyList();
    }

    public CoverageList(List<Coverage> coverages) {
        this.dao       = null;
        this.coverages = Collections.unmodifiableList(coverages);
    }

    private static Coverage toDomain(CoverageVO vo) {
        if (vo == null) return null;
        Coverage cov = new Coverage();
        cov.setCoverageId(vo.coverageId);
        cov.setCoverageName(vo.coverageName);
        cov.setMandatory(vo.mandatory);
        if (vo.coverageType != null) cov.setCoverageType(CoverageType.valueOf(vo.coverageType));
        if (vo.limitOptions != null) {
            cov.setLimitOptions(vo.limitOptions.stream().map(CoverageList::toLimitOption).collect(Collectors.toList()));
        }
        return cov;
    }

    private static CoverageLimitOption toLimitOption(CoverageLimitOptionVO vo) {
        CoverageLimitOption opt = new CoverageLimitOption();
        opt.setCoverageMasterId(vo.coverageMasterId);
        opt.setOptionId(vo.optionId);
        opt.setOptionName(vo.optionName);
        return opt;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public CoverageList findAll() {
        return new CoverageList(dao.findAll().stream().map(CoverageList::toDomain).collect(Collectors.toList()));
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Coverage> getAll() { return coverages; }
    public boolean isEmpty() { return coverages.isEmpty(); }
    public int size() { return coverages.size(); }
    public Coverage get(int index) { return coverages.get(index); }

    public Coverage findById(String coverageId) {
        if (!coverages.isEmpty()) {
            return coverages.stream()
            .filter(c -> coverageId.equals(c.getCoverageId()))
            .findFirst()
            .orElse(null);
        }
        return toDomain(dao.findById(coverageId));
    }

    public Map<String, Object> findByIdAsMap(String coverageId) {
        return toMap(findById(coverageId));
    }

    public void saveNew(Coverage coverage) {
        dao.saveNew(toRequestMap(coverage));
    }

    public void updateById(String coverageId, Coverage coverage) {
        dao.updateById(coverageId, toRequestMap(coverage));
    }

    public void delete(String coverageId) {
        dao.delete(coverageId);
    }

    @SuppressWarnings("unchecked")
    public static Coverage fromMap(Map<?, ?> data) {
        Coverage c = new Coverage();
        c.setCoverageId(str(data.get("coverageId")));
        c.setCoverageName(data.get("coverageName") != null ? str(data.get("coverageName")) : str(data.get("name")));
        if (data.get("coverageType") != null) {
            try { c.setCoverageType(CoverageType.valueOf(str(data.get("coverageType")))); } catch (Exception ignored) {}
        }
        c.setMandatory(Boolean.TRUE.equals(data.get("mandatory")));
        if (data.get("limitOptions") instanceof List<?> raw) {
            c.setLimitOptions(raw.stream()
                    .filter(Map.class::isInstance)
                    .map(o -> optionFromMap((Map<String, Object>) o))
                    .collect(Collectors.toList()));
        }
        return c;
    }

    public static Map<String, Object> toMap(Coverage c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",               c.getCoverageId());
        m.put("coverageId",       c.getCoverageId());
        m.put("name",             c.getCoverageName());
        m.put("coverageName",     c.getCoverageName());
        m.put("coverageType",     c.getCoverageType() != null ? c.getCoverageType().name() : null);
        m.put("mandatory",        c.isMandatory());
        m.put("description",      null);
        m.put("limitType",        null);
        m.put("limitAmount",      null);
        m.put("limitUnit",        null);
        m.put("compensationType", null);
        m.put("deductibleType",   null);
        m.put("deductibleAmount", null);
        m.put("deductibleRate",   null);
        m.put("autoRestoration",  false);
        m.put("excessPay",        false);
        m.put("provisionId",      null);
        m.put("exclusions",       List.of());
        m.put("requiredCoverages", List.of());
        m.put("limitOptions", c.getLimitOptions() != null
                ? c.getLimitOptions().stream().map(CoverageList::optionToMap).collect(Collectors.toList())
                : List.of());
        return m;
    }

    private static Map<String, Object> toRequestMap(Coverage c) {
        Map<String, Object> m = toMap(c);
        m.put("limitOptions", c.getLimitOptions() != null
                ? c.getLimitOptions().stream()
                    .map(o -> Map.<String, Object>of("optionName", o.getOptionName() != null ? o.getOptionName() : ""))
                    .collect(Collectors.toList())
                : List.of());
        return m;
    }

    private static CoverageLimitOption optionFromMap(Map<String, Object> data) {
        CoverageLimitOption o = new CoverageLimitOption();
        o.setOptionName(str(data.get("optionName")));
        return o;
    }

    private static Map<String, Object> optionToMap(CoverageLimitOption o) {
        return Map.of(
                "id", (Object) o.getOptionId(),
                "optionName", o.getOptionName() != null ? o.getOptionName() : ""
        );
    }

    private static String str(Object v) {
        return v != null ? v.toString() : null;
    }
}
