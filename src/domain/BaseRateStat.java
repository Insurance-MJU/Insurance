package domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseRateStat {
    private Integer id;
    private String statType;
    private int statYear;
    private String dimension1;
    private String dimension2;
    private String dimension3;
    private long lossAmount;
    private int deathCount;
    private int injuryCount;
    private int totalLossCount;
    private int partialLossCount;

    public static BaseRateStat fromMap(Map<?, ?> data) {
        BaseRateStat s = new BaseRateStat();
        s.id = intObject(data.get("id"));
        s.statType = str(data.get("statType"));
        s.statYear = intValue(data.get("statYear"));
        s.dimension1 = str(data.get("dimension1"));
        s.dimension2 = str(data.get("dimension2"));
        s.dimension3 = str(data.get("dimension3"));
        s.lossAmount = longValue(data.get("lossAmount"));
        s.deathCount = intValue(data.get("deathCount"));
        s.injuryCount = intValue(data.get("injuryCount"));
        s.totalLossCount = intValue(data.get("totalLossCount"));
        s.partialLossCount = intValue(data.get("partialLossCount"));
        return s;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("statType", statType);
        m.put("statYear", statYear);
        m.put("dimension1", dimension1);
        m.put("dimension2", dimension2);
        m.put("dimension3", dimension3);
        m.put("lossAmount", lossAmount);
        m.put("deathCount", deathCount);
        m.put("injuryCount", injuryCount);
        m.put("totalLossCount", totalLossCount);
        m.put("partialLossCount", partialLossCount);
        return m;
    }

    private static String str(Object v) { return v != null ? v.toString() : null; }
    private static Integer intObject(Object v) { return v != null ? intValue(v) : null; }
    private static int intValue(Object v) { return v != null ? (int) Double.parseDouble(v.toString()) : 0; }
    private static long longValue(Object v) { return v != null ? Long.parseLong(v.toString()) : 0L; }
}
