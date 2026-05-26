package domain;

import infra.dao.CoverageDao;

import java.util.Collections;
import java.util.List;

public class CoverageList {
    private final CoverageDao dao;
    private final List<Coverage> coverages;

    public CoverageList(CoverageDao dao) {
        this.dao = dao;
        this.coverages = Collections.emptyList();
    }

    public CoverageList(List<Coverage> coverages) {
        this.dao = null;
        this.coverages = Collections.unmodifiableList(coverages);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public CoverageList findAll() {
        return new CoverageList(dao.findAll());
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
