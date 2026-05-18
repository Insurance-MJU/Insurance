package domain;

public enum DiscountItem {
    MILEAGE("마일리지 가입", 0.047),
    NO_ACCIDENT_3Y("3년 무사고 할인", 0.030),
    ABS("ABS 특별요율", 0.020);

    private final String label;
    private final double rate;

    DiscountItem(String label, double rate) {
        this.label = label;
        this.rate  = rate;
    }

    public String getLabel() { return label; }
    public double getRate()  { return rate; }
}
