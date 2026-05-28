package controller.web.dto;

import domain.Claim;

public record ClaimResponse(
        String claimId,
        String customerName,
        String status,
        String assignedEmployee,
        String accidentId,
        String contractId,
        String receivedDate
) {
    public static ClaimResponse from(Claim c) {
        return new ClaimResponse(
                c.getClaimId(),
                c.getClaimantName(),
                c.getClaimStatus() != null ? c.getClaimStatus().name() : null,
                c.getAssignedEmployee(),
                c.getAccidentId(),
                c.getContractId(),
                c.getClaimDateDisplay()
        );
    }
}
