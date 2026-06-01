package infra.vo;

public class EmployeeVO {
    public final String employeeId;
    public final String name;
    public final String specialty;
    public final int openCaseCount;

    public EmployeeVO(String employeeId, String name, String specialty, int openCaseCount) {
        this.employeeId    = employeeId;
        this.name          = name;
        this.specialty     = specialty;
        this.openCaseCount = openCaseCount;
    }
}
