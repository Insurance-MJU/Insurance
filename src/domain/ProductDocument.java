package domain;

import java.io.Serializable;
import java.util.Date;

public class ProductDocument implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date createdAt;
    private DocType docType;
    private String note;
    private String productDocumentId;
    private String productId;
    private Date receivedAt;
    private Date submittedAt;
    private String title;

    public enum DocType {
        GENERAL_TERMS("보험약관"),
        SPECIAL_TERMS("특별약관"),
        BASIC_DOCUMENT("사업방법서"),
        RATE_CALC_BASIS("산출방법서"),
        RATE_VERIFICATION("요율확인서"),
        APPROVAL_APPLICATION("보험상품 인가신청서"),
        SALE_NOTIFICATION("상품 신고서"),
        PROFITABILITY_REPORT("수익성 분석 보고서"),
        DISCLOSURE("공시자료");

        private final String label;
        DocType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // ── 정적 팩토리 ───────────────────────────────────────────
    public static ProductDocument create(String productId, DocType docType, String title, String filePath) {
        ProductDocument doc = new ProductDocument();
        doc.productDocumentId = "DOC-" + System.nanoTime();
        doc.productId  = productId;
        doc.docType    = docType;
        doc.title      = title;
        doc.note       = filePath;
        doc.createdAt  = new Date();
        return doc;
    }

    public static ProductDocument createSubmitted(String productId, DocType docType, String title, String filePath) {
        ProductDocument doc = create(productId, docType, title, filePath);
        doc.submittedAt = new Date();
        return doc;
    }

    public static ProductDocument createReceived(String productId, DocType docType, String title, String filePath) {
        ProductDocument doc = create(productId, docType, title, filePath);
        doc.receivedAt = new Date();
        return doc;
    }

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
