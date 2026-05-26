package domain;

import infra.dao.AccidentDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccidentList {
    private final AccidentDao dao;
    private final List<Accident> accidents;

    public AccidentList(AccidentDao dao) {
        this.dao = dao;
        this.accidents = Collections.emptyList();
    }

    public AccidentList(List<Accident> accidents) {
        this.dao = null;
        this.accidents = Collections.unmodifiableList(accidents);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public AccidentList findByDateAndStatus(String date, String status) {
        return dao.findByDateAndStatus(date, status);
    }

    public AccidentList findPendingAccidents() {
        return dao.findPendingAccidents();
    }

    public Accident findById(String accidentId) {
        return dao.findById(accidentId);
    }

    public Accident findByCustomerName(String name) {
        return dao.findByCustomerName(name);
    }

    public String nextId() {
        return dao.nextId();
    }

    public void save(Accident accident) {
        dao.save(accident);
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Accident> getAll() { return accidents; }
    public boolean isEmpty() { return accidents.isEmpty(); }
    public int size() { return accidents.size(); }

    public AccidentList filterByStatus(AccidentStatus status) {
        return new AccidentList(
            accidents.stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList())
        );
    }

    public AccidentList pendingOnly() {
        return filterByStatus(AccidentStatus.PENDING);
    }
}
