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

    public enum DocType {}

    public Date getCreatedAt() { return createdAt; }
    public DocType getDocType() { return docType; }
    public String getNote() { return note; }
    public String getProductDocumentId() { return productDocumentId; }
    public String getProductId() { return productId; }
    public Date getReceivedAt() { return receivedAt; }
    public Date getSubmittedAt() { return submittedAt; }
    public String getTitle() { return title; }
}
