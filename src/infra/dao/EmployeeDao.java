package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.EmployeeVO;

import java.util.List;

public class EmployeeDao {
    private final Database db;

    public EmployeeDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<EmployeeVO> EXTRACTOR = rs ->
        new EmployeeVO(
            rs.getString("employee_id"),
            rs.getString("name"),
            rs.getString("specialty"),
            rs.getInt("open_case_count")
        );

    public List<EmployeeVO> findBySpecialty(String specialty) {
        if (specialty == null || specialty.isEmpty()) {
            return db.queryForList("SELECT * FROM employees", EXTRACTOR);
        }
        return db.queryForList(
            "SELECT * FROM employees WHERE specialty = ?",
            EXTRACTOR, specialty);
    }

    public EmployeeVO findById(String employeeId) {
        return db.queryForObject(
            "SELECT * FROM employees WHERE employee_id = ?",
            EXTRACTOR, employeeId);
    }
}
