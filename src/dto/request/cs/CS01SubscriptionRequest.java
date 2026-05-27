package dto.request.cs;

import java.util.List;

/**
 * CS-01 상품가입 요청
 * - name/ssn/phone 은 본인인증(IdentityVerificationService) 결과값
 * - driverScopeCode: "SELF" | "FAMILY"
 * - purposeCode: "COMMUTE" | "BUSINESS" | "COMMERCIAL"
 */
public record CS01SubscriptionRequest(
    // 본인 인증 결과
    String name,
    String ssn,
    String phone,

    // 상품 선택
    String productCode,

    // 차량 정보
    String carNumber,

    // 운행 정보
    String purposeCode,
    String driverScopeCode,

    // 가족한정 시 추가 (driverScopeCode = "FAMILY" 일 때만 사용)
    String familyName,
    String familyRelation,
    String familyBirthDate,

    // 동의 여부
    boolean agreedToPrivacy
) {}
