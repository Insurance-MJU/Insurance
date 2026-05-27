package dto.request.cs;

/**
 * CS-03 예상 보험료 산출 요청
 * - purposeCode: "COMMUTE" | "BUSINESS" | "COMMERCIAL"
 */
public record CS03PremiumEstimateRequest(
    String productCode,
    long   carStandardValue,  // 차량기준가액 (원 단위)
    String purposeCode
) {}
