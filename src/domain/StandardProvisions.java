package domain;

public class StandardProvisions {
    private String description;
    private String standardProvisionId;
    private String title;

    public String getDescription()         { return description; }
    public String getStandardProvisionId() { return standardProvisionId; }
    public String getTitle()               { return title; }

    public void setDescription(String v)         { this.description = v; }
    public void setStandardProvisionId(String v) { this.standardProvisionId = v; }
    public void setTitle(String v)               { this.title = v; }
}
