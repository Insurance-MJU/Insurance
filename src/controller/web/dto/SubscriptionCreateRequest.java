package controller.web.dto;

public record SubscriptionCreateRequest(
        String verificationToken,
        String productId,
        String address,
        String carNumber,
        String chassisNumber,
        String occupation,
        String carPurpose,
        String driverScope,
        long   premium
) {}
