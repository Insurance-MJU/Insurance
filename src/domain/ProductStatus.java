package domain;

public enum ProductStatus {
    DESIGNING("설계 중"),
    KIDI_SUBMITTED("보험개발원 제출"),
    KIDI_CONFIRMED("요율확인서 수령"),
    FSS_APPLIED("금감원 인가신청"),
    FSS_APPROVED("금감원 인가완료"),
    FILING("판매신고 중"),
    FILED("판매 확정"),
    ON_SALE("판매 중"),
    SALE_EXPIRED("판매기간 만료"),
    DISCONTINUED("판매 중단");

    private final String label;
    ProductStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
