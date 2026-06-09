package domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StandardProvisions {
    private String description;
    private List<Exclusion> exclusions;
    private String standardProvisionId;
    private String title;
    private Integer id;
    private String provisionType;

    public static StandardProvisions fromMap(Map<?, ?> data) {
        StandardProvisions p = new StandardProvisions();
        p.id = intObject(data.get("id"));
        p.standardProvisionId = data.get("id") != null ? data.get("id").toString() : str(data.get("standardProvisionId"));
        p.provisionType = str(data.get("provisionType"));
        p.title = str(data.get("title"));
        p.description = str(data.get("description"));
        return p;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("provisionType", provisionType);
        m.put("title", title);
        m.put("description", description);
        return m;
    }

    private static String str(Object v) { return v != null ? v.toString() : null; }
    private static Integer intObject(Object v) { return v != null ? (int) Double.parseDouble(v.toString()) : null; }

    public String getDescription()             { return description; }
    public List<Exclusion> getExclusions()     { return exclusions; }
    public String getStandardProvisionId()     { return standardProvisionId; }
    public String getTitle()                   { return title; }
    public Integer getId()                     { return id; }
    public String getProvisionType()           { return provisionType; }

    public void setDescription(String v)             { this.description = v; }
    public void setExclusions(List<Exclusion> v)     { this.exclusions = v; }
    public void setStandardProvisionId(String v)     { this.standardProvisionId = v; }
    public void setTitle(String v)                   { this.title = v; }
    public void setId(Integer v)                     { this.id = v; }
    public void setProvisionType(String v)           { this.provisionType = v; }
}
