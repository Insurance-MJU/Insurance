package domain;

public class Exclusion {
    private String description;
    private String exclusionId;
    private String exclusionName;
    private ExclusionType exclusionType;

    public enum ExclusionType {}

    public String getDescription() { return description; }
    public String getExclusionId() { return exclusionId; }
    public String getExclusionName() { return exclusionName; }
    public ExclusionType getExclusionType() { return exclusionType; }
}
