package domain;

import common.exception.domain.NotFoundException;
import domain.common.Money;
import infra.dao.AccidentDao;
import infra.vo.AccidentVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccidentList {
    private final AccidentDao dao;
    private final List<Accident> accidents;

    public AccidentList(AccidentDao dao) {
        this.dao       = dao;
        this.accidents = Collections.emptyList();
    }

    public AccidentList(List<Accident> accidents) {
        this.dao       = null;
        this.accidents = Collections.unmodifiableList(accidents);
    }

    private static Accident toDomain(AccidentVO vo) {
        if (vo == null) return null;
        Accident a = new Accident();
        a.setAccidentId(vo.accidentId);
        a.setUserId(vo.userId);
        a.setAccidentDate(vo.accidentDate);
        a.setReportedBy(vo.reportedBy);
        a.setPhone(vo.phone);
        a.setDescription(vo.description);
        a.setAccidentLocation(vo.accidentLocation);
        a.setAccidentDetail(vo.accidentDetail);
        a.setDocuments(vo.documents);
        a.setContractId(vo.contractId);
        a.setCoverageDescription(vo.coverageDescription);
        a.setCoverageLimit(new Money(vo.coverageLimit, "KRW"));
        a.setPersonalInjuryLimit(new Money(vo.personalInjuryLimit, "KRW"));
        a.setVehicleInfo(vo.vehicleInfo);
        a.setExpectedRepairCost(new Money(vo.expectedRepairCost, "KRW"));
        a.setRegionCode(vo.regionCode);
        if (vo.status != null) a.setStatus(AccidentStatus.valueOf(vo.status));
        return a;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public AccidentList findByDateAndStatus(String date, String status) {
        return new AccidentList(dao.findByDateAndStatus(date, status).stream()
            .map(AccidentList::toDomain).collect(Collectors.toList()));
    }

    public AccidentList findPendingAccidents() {
        return new AccidentList(dao.findPendingAccidents().stream()
            .map(AccidentList::toDomain).collect(Collectors.toList()));
    }

    public Accident findById(String accidentId) {
        return toDomain(dao.findById(accidentId));
    }

    public Accident getById(String accidentId) {
        Accident a = findById(accidentId);
        if (a == null) throw new NotFoundException("사고를 찾을 수 없습니다: " + accidentId);
        return a;
    }

    public Accident findByCustomerName(String name) {
        return toDomain(dao.findByCustomerName(name));
    }

    public AccidentList findByReportedBy(String reportedBy) {
        return new AccidentList(dao.findByReportedBy(reportedBy).stream()
            .map(AccidentList::toDomain).collect(Collectors.toList()));
    }

    public AccidentList findByUserId(String userId) {
        return new AccidentList(dao.findByUserId(userId).stream()
            .map(AccidentList::toDomain).collect(Collectors.toList()));
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
