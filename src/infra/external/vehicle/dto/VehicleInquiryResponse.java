package infra.external.vehicle.dto;

public record VehicleInquiryResponse(
        String carNumber,
        String manufacturer,
        String modelName,
        String modelType,
        int    modelYear,
        int    engineCC,
        String fuelType,
        long   standardValue,
        boolean hasABS,
        int    airbagCount,
        boolean hasBlackbox,
        String inquiredAt,
        String errorReason
) {
    public boolean isSuccess() { return errorReason == null; }
}
