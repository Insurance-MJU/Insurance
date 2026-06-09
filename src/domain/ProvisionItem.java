package domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProvisionItem {
    private Integer id;
    private Integer provisionId;
    private int articleNo;
    private String articleTitle;
    private String content;
    private Integer parentId;

    public static ProvisionItem fromMap(Map<?, ?> data) {
        ProvisionItem item = new ProvisionItem();
        item.id = intObject(data.get("id"));
        item.provisionId = intObject(data.get("provisionId"));
        item.articleNo = intValue(data.get("articleNo"));
        item.articleTitle = str(data.get("articleTitle"));
        item.content = str(data.get("content"));
        item.parentId = intObject(data.get("parentId"));
        return item;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("provisionId", provisionId);
        m.put("articleNo", articleNo);
        m.put("articleTitle", articleTitle);
        m.put("content", content);
        m.put("parentId", parentId);
        return m;
    }

    private static String str(Object v) { return v != null ? v.toString() : null; }
    private static Integer intObject(Object v) { return v != null ? intValue(v) : null; }
    private static int intValue(Object v) { return v != null ? (int) Double.parseDouble(v.toString()) : 0; }
}
