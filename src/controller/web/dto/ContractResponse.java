package controller.web.dto;

import domain.Contract;

public record ContractResponse(
        String contractId,
        String policyNo,
        String productName,
        long   premium,
        String status,
        String issueDate,
        String startDate,
        String endDate,
        String carNumber,
        String holderName,
        String subscriptionNo
) {
    public static ContractResponse from(Contract c) {
        return new ContractResponse(
                c.getContractId(),
                c.getPolicyNo(),
                c.getProductName(),
                c.getPremium() != null ? c.getPremium().getAmount() : 0L,
                c.getStatus() != null ? c.getStatus().name() : null,
                c.getIssueDate() != null ? c.getIssueDate().toString() : null,
                c.getStartDate() != null ? c.getStartDate().toString() : null,
                c.getEndDate()   != null ? c.getEndDate().toString()   : null,
                c.getCarNumber(),
                c.getPolicyholder() != null ? c.getPolicyholder().getName() : null,
                c.getSubscriptionNo()
        );
    }
}
