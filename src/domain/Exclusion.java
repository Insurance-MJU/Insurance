package domain;

public class Exclusion {
    private String description;
    private String exclusionId;
    private String exclusionName;

    public String getDescription()  { return description; }
    public String getExclusionId()  { return exclusionId; }
    public String getExclusionName() { return exclusionName; }

    public void setDescription(String v)   { this.description = v; }
    public void setExclusionId(String v)   { this.exclusionId = v; }
    public void setExclusionName(String v) { this.exclusionName = v; }
}
