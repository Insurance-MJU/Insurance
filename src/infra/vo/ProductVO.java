package infra.vo;

import java.util.Date;
import java.util.List;

public class ProductVO {
    public final String productId;
    public final String productCode;
    public final String productName;
    public final String description;
    public final String lineOfBusiness;
    public final Date saleStartDate;
    public final Date saleEndDate;
    public final String status;
    public final String target;
    public final Date createdAt;
    public final List<ProductCoverageVO> coverages;
    public final List<ProductRiderVO> riders;
    public final List<ProductDocumentVO> documents;

    public ProductVO(String productId, String productCode, String productName, String description,
                     String lineOfBusiness, Date saleStartDate, Date saleEndDate, String status,
                     String target, Date createdAt, List<ProductCoverageVO> coverages,
                     List<ProductRiderVO> riders, List<ProductDocumentVO> documents) {
        this.productId       = productId;
        this.productCode     = productCode;
        this.productName     = productName;
        this.description     = description;
        this.lineOfBusiness  = lineOfBusiness;
        this.saleStartDate   = saleStartDate;
        this.saleEndDate     = saleEndDate;
        this.status          = status;
        this.target          = target;
        this.createdAt       = createdAt;
        this.coverages       = coverages;
        this.riders          = riders;
        this.documents       = documents;
    }
}
