package infra.dao;

import domain.Employee;
import domain.FieldInvestigatorList;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

public class EmployeeDao {
    private final Database db;

    public EmployeeDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Employee.FieldInvestigator> EXTRACTOR = rs ->
        new Employee.FieldInvestigator(
            rs.getString("employee_id"),
            rs.getString("name"),
            rs.getString("specialty"),
            rs.getInt("open_case_count")
        );

    public FieldInvestigatorList findBySpecialty(String specialty) {
        if (specialty == null || specialty.isEmpty()) {
            return new FieldInvestigatorList(db.queryForList("SELECT * FROM employees", EXTRACTOR));
        }
        return new FieldInvestigatorList(db.queryForList(
            "SELECT * FROM employees WHERE specialty = ?",
            EXTRACTOR, specialty));
    }

    public Employee.FieldInvestigator findById(String employeeId) {
        return db.queryForObject(
            "SELECT * FROM employees WHERE employee_id = ?",
            EXTRACTOR, employeeId);
    }
}
