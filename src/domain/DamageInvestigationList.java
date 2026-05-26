package domain;

import infra.dao.DamageInvestigationDao;

import java.util.Collections;
import java.util.List;

public class DamageInvestigationList {
    private final DamageInvestigationDao dao;
    private final List<DamageInvestigation> investigations;

    public DamageInvestigationList(DamageInvestigationDao dao) {
        this.dao = dao;
        this.investigations = Collections.emptyList();
    }

    public DamageInvestigationList(List<DamageInvestigation> investigations) {
        this.dao = null;
        this.investigations = Collections.unmodifiableList(investigations);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public DamageInvestigation findByAccidentId(String accidentId) {
        return dao.findByAccidentId(accidentId);
    }

    public void save(DamageInvestigation inv) {
        dao.save(inv);
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<DamageInvestigation> getAll() { return investigations; }
    public boolean isEmpty() { return investigations.isEmpty(); }
    public int size() { return investigations.size(); }
}
