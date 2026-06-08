package infra.vo;

import java.util.Date;

public class ProductDocumentVO {
    public final String productDocumentId;
    public final String productId;
    public final String docType;
    public final String title;
    public final String note;
    public final String filename;
    public final String filePath;
    public final Date createdAt;
    public final Date submittedAt;
    public final Date receivedAt;

    public ProductDocumentVO(String productDocumentId, String productId, String docType,
                             String title, String note, String filename, String filePath,
                             Date createdAt, Date submittedAt, Date receivedAt) {
        this.productDocumentId = productDocumentId;
        this.productId         = productId;
        this.docType           = docType;
        this.title             = title;
        this.note              = note;
        this.filename          = filename;
        this.filePath          = filePath;
        this.createdAt         = createdAt;
        this.submittedAt       = submittedAt;
        this.receivedAt        = receivedAt;
    }
}
