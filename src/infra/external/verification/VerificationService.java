package infra.external.verification;

import infra.external.verification.dto.OtpSendRequest;
import infra.external.verification.dto.OtpSendResponse;
import infra.external.verification.dto.OtpVerifyRequest;
import infra.external.verification.dto.OtpVerifyResponse;
import infra.external.verification.dto.VerifiedIdentity;

/**
 * 본인인증 외부 서비스 인터페이스
 * 실제 구현체는 PASS, 카카오, 토스 등 본인인증 플랫폼과 연동
 */
public interface VerificationService {
    OtpSendResponse sendOtp(OtpSendRequest request);
    OtpVerifyResponse verifyOtp(OtpVerifyRequest request);
    VerifiedIdentity resolveIdentity(String verificationToken);
}
