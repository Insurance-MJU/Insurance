package infra.external.verification.mock;

import infra.external.verification.VerificationService;
import infra.external.verification.dto.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MockVerificationService implements VerificationService {

    private static final String MOCK_OTP = "123456";

    private final Map<String, OtpSession> sessions       = new ConcurrentHashMap<>();
    private final Map<String, VerifiedIdentity> verified = new ConcurrentHashMap<>();

    @Override
    public OtpSendResponse sendOtp(OtpSendRequest request) {
        String sessionId = UUID.randomUUID().toString();
        String expiresAt = LocalDateTime.now().plusMinutes(5).toString();
        sessions.put(sessionId, new OtpSession(
                MOCK_OTP, expiresAt,
                new VerifiedIdentity(request.name(), request.ssn(), request.phone())
        ));
        System.out.printf("[MockVerification] OTP sent: phone=%s otp=%s session=%s%n",
                request.phone(), MOCK_OTP, sessionId);
        return new OtpSendResponse(sessionId, expiresAt);
    }

    @Override
    public OtpVerifyResponse verifyOtp(OtpVerifyRequest request) {
        OtpSession session = sessions.get(request.sessionId());
        if (session == null)
            return OtpVerifyResponse.fail("유효하지 않은 인증 세션입니다.");
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(session.expiresAt()))) {
            sessions.remove(request.sessionId());
            return OtpVerifyResponse.fail("인증번호가 만료되었습니다.");
        }
        if (!MOCK_OTP.equals(request.otp()))
            return OtpVerifyResponse.fail("인증번호가 일치하지 않습니다.");

        sessions.remove(request.sessionId());
        String token = "VERIFY." + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        verified.put(token, session.identity());
        return OtpVerifyResponse.ok(token);
    }

    @Override
    public VerifiedIdentity resolveIdentity(String verificationToken) {
        VerifiedIdentity identity = verified.get(verificationToken);
        if (identity == null)
            throw new common.exception.infra.UnauthorizedException("유효하지 않은 인증 토큰입니다.");
        return identity;
    }

    private record OtpSession(String otp, String expiresAt, VerifiedIdentity identity) {}
}
