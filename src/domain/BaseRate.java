package domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseRate {
    private Integer id;
    private String rateType;
    private String dimension1;
    private String dimension2;
    private double rateValue;
    private int effectiveYear;
    private String note;

    public static BaseRate fromMap(Map<?, ?> data) {
        BaseRate r = new BaseRate();
        r.setId(intObject(data.get("id")));
        r.setRateType(str(data.get("rateType")));
        r.setDimension1(str(data.get("dimension1")));
        r.setDimension2(str(data.get("dimension2")));
        r.setRateValue(doubleValue(data.get("rateValue")));
        r.setEffectiveYear(intValue(data.get("effectiveYear")));
        r.setNote(str(data.get("note")));
        return r;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("rateType", rateType);
        m.put("dimension1", dimension1);
        m.put("dimension2", dimension2);
        m.put("rateValue", rateValue);
        m.put("effectiveYear", effectiveYear);
        m.put("note", note);
        return m;
    }

    private static String str(Object v) {
        return v != null ? v.toString() : null;
    }

    private static Integer intObject(Object v) {
        return v != null ? intValue(v) : null;
    }

    private static int intValue(Object v) {
        return v != null ? (int) Double.parseDouble(v.toString()) : 0;
    }

    private static double doubleValue(Object v) {
        return v != null ? Double.parseDouble(v.toString()) : 0.0;
    }

    public Integer getId() { return id; }
    public String getRateType() { return rateType; }
    public String getDimension1() { return dimension1; }
    public String getDimension2() { return dimension2; }
    public double getRateValue() { return rateValue; }
    public int getEffectiveYear() { return effectiveYear; }
    public String getNote() { return note; }

    public void setId(Integer id) { this.id = id; }
    public void setRateType(String rateType) { this.rateType = rateType; }
    public void setDimension1(String dimension1) { this.dimension1 = dimension1; }
    public void setDimension2(String dimension2) { this.dimension2 = dimension2; }
    public void setRateValue(double rateValue) { this.rateValue = rateValue; }
    public void setEffectiveYear(int effectiveYear) { this.effectiveYear = effectiveYear; }
    public void setNote(String note) { this.note = note; }
}
