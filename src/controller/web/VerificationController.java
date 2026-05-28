package controller.web;

import infra.external.verification.VerificationService;
import infra.external.verification.dto.OtpSendRequest;
import infra.external.verification.dto.OtpVerifyRequest;
import infra.web.Router;

public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public void registerRoutes(Router router) {
        router.post("/verification/send-otp",   (req, res) -> res.ok(verificationService.sendOtp(req.body(OtpSendRequest.class))));
        router.post("/verification/verify-otp", (req, res) -> res.ok(verificationService.verifyOtp(req.body(OtpVerifyRequest.class))));
    }
}
