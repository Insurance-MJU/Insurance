package infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeRepository {

    public static class FieldInvestigator {
        private final String employeeId;
        private final String name;
        private final String specialty;
        private final int openCaseCount;

        public FieldInvestigator(String employeeId, String name,
                                 String specialty, int openCaseCount) {
            this.employeeId    = employeeId;
            this.name          = name;
            this.specialty     = specialty;
            this.openCaseCount = openCaseCount;
        }

        public String getEmployeeId()    { return employeeId; }
        public String getName()          { return name; }
        public String getSpecialty()     { return specialty; }
        public int    getOpenCaseCount() { return openCaseCount; }
    }

    private static final List<FieldInvestigator> STORE = new ArrayList<>();

    static {
        STORE.add(new FieldInvestigator("EMP-1023", "이현수", "자동차 대물",  2));
        STORE.add(new FieldInvestigator("EMP-1045", "박지영", "자동차 대물",  4));
        STORE.add(new FieldInvestigator("EMP-1067", "최준호", "자기차량손해", 1));
        STORE.add(new FieldInvestigator("EMP-1082", "정다은", "자동차 대물",  3));
    }

    public static List<FieldInvestigator> findBySpecialty(String specialty) {
        return STORE.stream()
            .filter(e -> specialty.isEmpty() || e.getSpecialty().equals(specialty))
            .collect(Collectors.toList());
    }

    public static FieldInvestigator findById(String employeeId) {
        return STORE.stream()
            .filter(e -> e.getEmployeeId().equals(employeeId))
            .findFirst().orElse(null);
    }
}
