package controller.web.dto;

import domain.Subscription;

public record SubscriptionResponse(
        String subscriptionNo,
        String applicantName,
        String ssn,
        String carNumber,
        String productName,
        long premium,
        String status,
        String subscriptionDate,
        String contractId,
        String contractStatus
) {
    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(
                s.getSubscriptionNo(),
                s.getApplicantName(),
                s.getSsn(),
                s.getCarNumber(),
                s.getProductName(),
                s.getPremium() != null ? s.getPremium().getAmount() : 0,
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getSubscriptionDateDisplay(),
                null,
                null
        );
    }

    public static SubscriptionResponse from(Subscription s, String contractId, String contractStatus) {
        return new SubscriptionResponse(
                s.getSubscriptionNo(),
                s.getApplicantName(),
                s.getSsn(),
                s.getCarNumber(),
                s.getProductName(),
                s.getPremium() != null ? s.getPremium().getAmount() : 0,
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getSubscriptionDateDisplay(),
                contractId,
                contractStatus
        );
    }
}
