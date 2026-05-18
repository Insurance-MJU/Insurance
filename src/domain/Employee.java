package domain;

public class Employee {

    // ── DAO 위임 ──────────────────────────────────────────────
    public static java.util.List<FieldInvestigator> findBySpecialty(String specialty) { return infra.dao.EmployeeDao.getInstance().findBySpecialty(specialty); }
    public static FieldInvestigator findById(String employeeId)                       { return infra.dao.EmployeeDao.getInstance().findById(employeeId); }

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
}
