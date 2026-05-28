package controller.web;

import infra.external.vehicle.VehicleInquiryService;
import infra.external.vehicle.dto.VehicleInquiryRequest;
import infra.web.Router;

public class VehicleController {

    private final VehicleInquiryService vehicleService;

    public VehicleController(VehicleInquiryService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public void registerRoutes(Router router) {
        router.get("/vehicles/{carNo}", (req, res) -> res.ok(vehicleService.inquire(new VehicleInquiryRequest(req.pathVariable("carNo")))));
    }
}
