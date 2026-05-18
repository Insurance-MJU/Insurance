package infra.dao;

import domain.Employee;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeDao {
    private static final EmployeeDao INSTANCE = new EmployeeDao();
    public static EmployeeDao getInstance() { return INSTANCE; }

    private static final List<Employee.FieldInvestigator> STORE = new ArrayList<>();
    static {
        STORE.add(new Employee.FieldInvestigator("EMP-1023", "이현수", "자동차 대물",  2));
        STORE.add(new Employee.FieldInvestigator("EMP-1045", "박지영", "자동차 대물",  4));
        STORE.add(new Employee.FieldInvestigator("EMP-1067", "최준호", "자기차량손해", 1));
        STORE.add(new Employee.FieldInvestigator("EMP-1082", "정다은", "자동차 대물",  3));
    }

    public List<Employee.FieldInvestigator> findBySpecialty(String specialty) {
        return STORE.stream()
            .filter(e -> specialty.isEmpty() || e.getSpecialty().equals(specialty))
            .collect(Collectors.toList());
    }

    public Employee.FieldInvestigator findById(String employeeId) {
        return STORE.stream()
            .filter(e -> e.getEmployeeId().equals(employeeId))
            .findFirst().orElse(null);
    }
}
