package controller.web.dto;

import domain.Employee;

public record InvestigatorResponse(
        String employeeId,
        String name,
        String specialty,
        int openCaseCount
) {
    public static InvestigatorResponse from(Employee.FieldInvestigator i) {
        return new InvestigatorResponse(
                i.getEmployeeId(),
                i.getName(),
                i.getSpecialty(),
                i.getOpenCaseCount()
        );
    }
}
