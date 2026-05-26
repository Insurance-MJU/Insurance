package domain;

public class Employee {

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
