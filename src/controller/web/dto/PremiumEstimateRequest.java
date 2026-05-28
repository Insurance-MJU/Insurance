package controller.web.dto;

public record PremiumEstimateRequest(
        String productId,
        long carStandardValue,
        String carPurpose,
        int driverAge
) {}
