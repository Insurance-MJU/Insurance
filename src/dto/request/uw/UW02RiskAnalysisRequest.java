package dto.request.uw;

/**
 * UW-02 위험성 분석 요청
 * - 단독 실행 시 name/ssn/carNumber 직접 입력
 * - UW-01 include 시 subscriptionNo 로 조회
 */
public record UW02RiskAnalysisRequest(
    String subscriptionNo,  // include 경로: UW-01에서 전달
    String name,            // 단독 경로: 직접 입력
    String ssn,
    String carNumber
) {}
