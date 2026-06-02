package infra.dao;

import domain.VehicleInfo;
import infra.external.vehicle.VehicleInquiryService;
import infra.external.vehicle.dto.VehicleInquiryRequest;
import infra.external.vehicle.dto.VehicleInquiryResponse;

public class CarDao {
    private final VehicleInquiryService vehicleService;

    public CarDao(VehicleInquiryService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public VehicleInfo findByCarNumber(String carNumber) {
        VehicleInquiryResponse r = vehicleService.inquire(new VehicleInquiryRequest(carNumber));
        if (!r.isSuccess()) {
            return VehicleInfo.notFound(carNumber, r.errorReason());
        }
        return new VehicleInfo(
            r.carNumber(), r.manufacturer(), r.modelName(), r.modelType(),
            r.modelYear(), r.engineCC(), r.fuelType(), r.standardValue(),
            r.hasABS(), r.hasBlackbox()
        );
    }
}
