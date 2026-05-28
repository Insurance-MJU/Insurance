package controller.web.dto;

public record SubscriptionCreateRequest(
        String productId,
        String applicantName,
        String ssn,
        String address,
        String carNumber,
        String chassisNumber,
        String occupation,
        int age,
        String carPurpose,
        String driverScope,
        long premium
) {}
