package infra.external.kidi;

/**
 * 보험개발원(KIDI) 외부 서비스 인터페이스
 * 요율 검증 신청을 처리
 */
public interface KidiService {
    boolean submitRateVerification(String productId);
}
