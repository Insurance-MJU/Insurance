package domain;

import java.util.Date;

public class ClaimDocument {
    private String claimId;
    private String documentId;
    private String documentName;
    private DocumentType documentType;
    private String filePath;
    private Date uploadDate;
    private boolean verified;

    public String getDocument() { return null; }
    public boolean uploadDocument(String filePath) { return false; }
    public boolean verifyDocument() { return false; }

    public String getClaimId() { return claimId; }
    public String getDocumentId() { return documentId; }
    public String getDocumentName() { return documentName; }
    public DocumentType getDocumentType() { return documentType; }
    public String getFilePath() { return filePath; }
    public Date getUploadDate() { return uploadDate; }
    public boolean isVerified() { return verified; }
}
