package domain;

import java.util.Date;

public class ClaimDocument {
    private Claim claim;
    private String documentId;
    private String documentName;
    private String filePath;
    private Date uploadDate;
    private boolean verified;

    public String getClaimId()       { return claim != null ? claim.getClaimId() : null; }
    public Claim getClaim()          { return claim; }
    public String getDocumentId()    { return documentId; }
    public String getDocumentName()  { return documentName; }
    public String getFilePath()      { return filePath; }
    public Date getUploadDate()      { return uploadDate; }
    public boolean isVerified()      { return verified; }

    public void setClaim(Claim v)          { this.claim = v; }
    public void setDocumentId(String v)    { this.documentId = v; }
    public void setDocumentName(String v)  { this.documentName = v; }
    public void setFilePath(String v)      { this.filePath = v; }
    public void setUploadDate(Date v)      { this.uploadDate = v; }
    public void setVerified(boolean v)     { this.verified = v; }
}
