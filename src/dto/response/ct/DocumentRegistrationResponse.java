package dto.response.ct;

import java.util.List;

/**
 * CT-03 기초서류 등록 완료 응답
 */
public record DocumentRegistrationResponse(
    String       productCode,
    List<String> registeredDocuments,
    String       statusLabel
) {}
