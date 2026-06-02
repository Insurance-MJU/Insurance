package infra.dao;

import domain.OtpSession;
import domain.OtpVerifyResult;
import domain.VerifiedUser;
import infra.external.verification.VerificationService;
import infra.external.verification.dto.OtpSendRequest;
import infra.external.verification.dto.OtpSendResponse;
import infra.external.verification.dto.OtpVerifyRequest;
import infra.external.verification.dto.OtpVerifyResponse;
import infra.external.verification.dto.VerifiedIdentity;

public class VerificationDao {
    private final VerificationService verificationService;

    public VerificationDao(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public OtpSession sendOtp(String name, String ssn, String phone, String method) {
        OtpSendResponse resp = verificationService.sendOtp(new OtpSendRequest(name, ssn, phone, method));
        return new OtpSession(resp.sessionId(), resp.expiresAt());
    }

    public OtpVerifyResult verifyOtp(String sessionId, String otp) {
        OtpVerifyResponse resp = verificationService.verifyOtp(new OtpVerifyRequest(sessionId, otp));
        if (resp.success()) {
            return OtpVerifyResult.ok(resp.verificationToken());
        }
        return OtpVerifyResult.fail(resp.errorMessage());
    }

    public VerifiedUser resolveIdentity(String verificationToken) {
        VerifiedIdentity identity = verificationService.resolveIdentity(verificationToken);
        return new VerifiedUser(identity.name(), identity.ssn(), identity.phone());
    }
}
