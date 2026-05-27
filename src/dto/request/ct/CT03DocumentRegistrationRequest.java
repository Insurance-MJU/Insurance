package dto.request.ct;

/**
 * CT-03 기초서류 등록 요청
 * - documentType: "BASIC" | "SPECIAL" | "RATE_TABLE" 등
 */
public record CT03DocumentRegistrationRequest(
    String productCode,
    String documentType,
    String fileName
) {}
