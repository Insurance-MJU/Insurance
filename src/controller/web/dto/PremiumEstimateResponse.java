package controller.web.dto;

public record PremiumEstimateResponse(
        long basePremium,
        long finalPremium,
        String currency
) {}
