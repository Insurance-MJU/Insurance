package domain;

import infra.dao.EmployeeDao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FieldInvestigatorList {
    private final EmployeeDao dao;
    private final List<Employee.FieldInvestigator> investigators;

    public FieldInvestigatorList(EmployeeDao dao) {
        this.dao = dao;
        this.investigators = Collections.emptyList();
    }

    public FieldInvestigatorList(List<Employee.FieldInvestigator> investigators) {
        this.dao = null;
        this.investigators = Collections.unmodifiableList(investigators);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public FieldInvestigatorList findBySpecialty(String specialty) {
        return dao.findBySpecialty(specialty);
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Employee.FieldInvestigator> getAll() { return investigators; }
    public boolean isEmpty() { return investigators.isEmpty(); }
    public int size() { return investigators.size(); }

    public Employee.FieldInvestigator leastBusy() {
        return investigators.stream()
            .min(Comparator.comparingInt(Employee.FieldInvestigator::getOpenCaseCount))
            .orElse(null);
    }

    public Employee.FieldInvestigator findById(String employeeId) {
        return investigators.stream()
            .filter(i -> employeeId.equals(i.getEmployeeId()))
            .findFirst()
            .orElse(null);
    }
}
