package domain;

public enum ProductStatus {
    DESIGN("설계중"), DESIGN_COMPLETE("설계완료"),
    APPROVAL_PENDING("인가신청중"), APPROVED("인가완료"),
    SALE_PENDING("판매신청중"), ON_SALE("판매중"), SALE_EXPIRED("판매기간만료"), DISCONTINUED("판매중지");
    private final String label;
    ProductStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}
