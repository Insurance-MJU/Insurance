package domain;

import infra.dao.CoverageDao;
import infra.vo.CoverageLimitOptionVO;
import infra.vo.CoverageVO;

import java.util.Collections;
import java.util.List;
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
        return coverages.stream()
            .filter(c -> coverageId.equals(c.getCoverageId()))
            .findFirst()
            .orElse(null);
    }
}
