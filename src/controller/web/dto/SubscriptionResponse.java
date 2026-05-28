package controller.web.dto;

import domain.Subscription;

public record SubscriptionResponse(
        String subscriptionNo,
        String applicantName,
        String productName,
        long premium,
        String status,
        String subscriptionDate,
        String contractId
) {
    public static SubscriptionResponse from(Subscription s) {
        return new SubscriptionResponse(
                s.getSubscriptionNo(),
                s.getApplicantName(),
                s.getProductName(),
                s.getPremium() != null ? s.getPremium().getAmount() : 0,
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getSubscriptionDateDisplay(),
                null
        );
    }

    public static SubscriptionResponse from(Subscription s, String contractId) {
        return new SubscriptionResponse(
                s.getSubscriptionNo(),
                s.getApplicantName(),
                s.getProductName(),
                s.getPremium() != null ? s.getPremium().getAmount() : 0,
                s.getStatus() != null ? s.getStatus().name() : null,
                s.getSubscriptionDateDisplay(),
                contractId
        );
    }
}
