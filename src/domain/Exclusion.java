package domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Exclusion {
    private String description;
    private String exclusionId;
    private String exclusionName;
    private Integer id;
    private String exclusionType;
    private String exclusionTypeDisplayName;
    private List<Map<String, Object>> subItems;

    @SuppressWarnings("unchecked")
    public static Exclusion fromMap(Map<?, ?> data) {
        Exclusion e = new Exclusion();
        e.id = intObject(data.get("id"));
        e.exclusionId = data.get("id") != null ? data.get("id").toString() : str(data.get("exclusionId"));
        e.exclusionType = str(data.get("exclusionType"));
        e.exclusionTypeDisplayName = str(data.get("exclusionTypeDisplayName"));
        e.exclusionName = data.get("name") != null ? str(data.get("name")) : str(data.get("exclusionName"));
        e.description = str(data.get("description"));
        e.subItems = data.get("subItems") instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();
        return e;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("exclusionType", exclusionType);
        m.put("exclusionTypeDisplayName", exclusionTypeDisplayName);
        m.put("name", exclusionName);
        m.put("description", description);
        m.put("subItems", subItems != null ? subItems : List.of());
        return m;
    }

    private static String str(Object v) { return v != null ? v.toString() : null; }
    private static Integer intObject(Object v) { return v != null ? (int) Double.parseDouble(v.toString()) : null; }

    public String getDescription()  { return description; }
    public String getExclusionId()  { return exclusionId; }
    public String getExclusionName() { return exclusionName; }
    public Integer getId() { return id; }
    public String getExclusionType() { return exclusionType; }
    public String getExclusionTypeDisplayName() { return exclusionTypeDisplayName; }
    public List<Map<String, Object>> getSubItems() { return subItems; }

    public void setDescription(String v)   { this.description = v; }
    public void setExclusionId(String v)   { this.exclusionId = v; }
    public void setExclusionName(String v) { this.exclusionName = v; }
    public void setId(Integer v) { this.id = v; }
    public void setExclusionType(String v) { this.exclusionType = v; }
    public void setExclusionTypeDisplayName(String v) { this.exclusionTypeDisplayName = v; }
    public void setSubItems(List<Map<String, Object>> v) { this.subItems = v; }
}
