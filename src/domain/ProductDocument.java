package domain;

import java.util.Date;

public class ProductDocument {
    private Date createdAt;
    private DocType docType;
    private String note;
    private String productDocumentId;
    private String productId;
    private Date receivedAt;
    private Date submittedAt;
    private String title;

    public enum DocType { GENERAL_TERMS, SPECIAL_TERMS, BASIC_DOCUMENT }

    // Setters
    public void setCreatedAt(Date v)            { this.createdAt = v; }
    public void setDocType(DocType v)           { this.docType = v; }
    public void setNote(String v)               { this.note = v; }
    public void setProductDocumentId(String v)  { this.productDocumentId = v; }
    public void setProductId(String v)          { this.productId = v; }
    public void setReceivedAt(Date v)           { this.receivedAt = v; }
    public void setSubmittedAt(Date v)          { this.submittedAt = v; }
    public void setTitle(String v)              { this.title = v; }

    // Getters
    public Date getCreatedAt()           { return createdAt; }
    public DocType getDocType()          { return docType; }
    public String getNote()              { return note; }
    public String getProductDocumentId() { return productDocumentId; }
    public String getProductId()         { return productId; }
    public Date getReceivedAt()          { return receivedAt; }
    public Date getSubmittedAt()         { return submittedAt; }
    public String getTitle()             { return title; }
}
