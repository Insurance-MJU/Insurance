package domain;

public enum Target {
    PERSONAL("만 20세 이상 39세 이하 개인", "개인용"),
    BUSINESS("업무용 차량 보유 사업자", "업무용"),
    COMMERCIAL("영업용 차량 보유자", "영업용");
    private final String description;
    private final String autoPrefix;
    Target(String description, String autoPrefix) {
        this.description = description;
        this.autoPrefix  = autoPrefix;
    }
    public String getDescription() { return description; }
    public String getAutoLabel()   { return autoPrefix + "자동차보험"; }
}
