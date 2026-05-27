package dto.request.cs;

/**
 * CS-02 보험상품 조회 요청
 * - keyword 없으면 전체 조회
 */
public record CS02ProductSearchRequest(
    String keyword   // nullable — 없으면 전체 조회
) {}
