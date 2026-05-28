package controller.web.dto;

import domain.Accident;

public record AccidentResponse(
        String accidentId,
        String reportedBy,
        String phone,
        String accidentDate,
        String accidentLocation,
        String status,
        String contractId
) {
    public static AccidentResponse from(Accident a) {
        return new AccidentResponse(
                a.getAccidentId(),
                a.getReportedBy(),
                a.getPhone(),
                a.getAccidentDateDisplay(),
                a.getAccidentLocation(),
                a.getStatusLabel(),
                a.getContractId()
        );
    }
}
