package controller.web.dto;

public record AccidentReportRequest(
        String reportedBy,
        String phone,
        String accidentDate,
        String accidentLocation,
        String accidentDetail,
        String documents,
        String contractId
) {}
