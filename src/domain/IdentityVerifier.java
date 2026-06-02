package domain;

import infra.dao.VerificationDao;

public class IdentityVerifier {
    private final VerificationDao dao;

    public IdentityVerifier(VerificationDao dao) {
        this.dao = dao;
    }

    public OtpSession sendOtp(String name, String ssn, String phone, String method) {
        return dao.sendOtp(name, ssn, phone, method);
    }

    public OtpVerifyResult verifyOtp(OtpSession session, String otp) {
        return dao.verifyOtp(session.getSessionId(), otp);
    }

    public VerifiedUser resolveIdentity(String verificationToken) {
        return dao.resolveIdentity(verificationToken);
    }
}
