package infra.external.credit.dto;

import java.util.List;

public record CreditInquiryResponse(
        String applicantName,
        String creditGrade,
        int    drivingExperienceYears,
        String fraudHistory,
        List<AccidentRecord> accidentHistory
) {
    public record AccidentRecord(String date, String description, long amountKrw) {}
}
