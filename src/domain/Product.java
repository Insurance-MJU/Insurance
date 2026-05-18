package domain;

import domain.exception.ValidationException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<ProductCoverage> coverages;
    private Date createdAt;
    private String description;
    private List<ProductDocument> documents;
    private LineOfBusiness lineOfBusiness;
    private String productCode;
    private String productId;
    private String productName;
    private List<ProductRider> riders;
    private Date saleEndDate;
    private Date saleStartDate;
    private ProductStatus status;
    private Target target;

    // ── 정적 팩토리: 신규 상품 설계 ──────────────────────────
    public static Product design(String productCode, String productName, String description,
                                  Target target, Date saleStart, Date saleEnd) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (productCode == null || productCode.isBlank())  errors.add("상품코드는 필수입니다");
        if (productName == null || productName.isBlank())  errors.add("상품명은 필수입니다");
        if (target == null)                                errors.add("가입대상은 필수입니다");
        if (saleStart == null)                             errors.add("판매시작일은 필수입니다");
        if (saleEnd == null)                               errors.add("판매종료일은 필수입니다");
        if (saleStart != null && saleEnd != null && !saleStart.before(saleEnd))
                                                           errors.add("판매종료일은 시작일 이후여야 합니다");
        if (!errors.isEmpty()) throw new ValidationException(errors);

        Product p = new Product();
        p.productId      = "PROD-" + System.currentTimeMillis();
        p.productCode    = productCode;
        p.productName    = productName;
        p.description    = description;
        p.target         = target;
        p.lineOfBusiness = LineOfBusiness.AUTO;
        p.saleStartDate  = saleStart;
        p.saleEndDate    = saleEnd;
        p.createdAt      = new Date();
        p.documents      = new ArrayList<>();
        p.coverages      = new ArrayList<>();
        p.riders         = new ArrayList<>();
        p.completeDesign();
        return p;
    }

    // ── 비즈니스 메서드: 서류 추가 ────────────────────────────
    public void addDocument(ProductDocument doc) {
        if (this.documents == null) this.documents = new ArrayList<>();
        this.documents.add(doc);
    }

    public void addDocuments(List<ProductDocument> docs) {
        if (this.documents == null) this.documents = new ArrayList<>();
        this.documents.addAll(docs);
    }

    public void discontinue()      { this.status = ProductStatus.DISCONTINUED; }
    public void ondesign()         { this.status = ProductStatus.DESIGN; }
    public void onsale()           { this.status = ProductStatus.ON_SALE; }
    public void completeDesign()   { this.status = ProductStatus.DESIGN_COMPLETE; }
    public void applyForApproval() { this.status = ProductStatus.APPROVAL_PENDING; }
    public void completeApproval() { this.status = ProductStatus.APPROVED; }
    public void applySalePermit()  { this.status = ProductStatus.SALE_PENDING; }

    /** 자동차보험 표준 담보 목록 문자열 — 의무 담보(대인I) 포함 전 담보 */
    public String getDefaultCoverageDescription() {
        return "대인배상I, 대인배상II, 대물배상, 자동차상해, 무보험차상해, 자기차량손해";
    }

    public boolean isOnSale() {
        if (status != ProductStatus.ON_SALE) return false;
        Date now = new Date();
        boolean afterStart = saleStartDate == null || !now.before(saleStartDate);
        boolean beforeEnd  = saleEndDate   == null || !now.after(saleEndDate);
        return afterStart && beforeEnd;
    }

    public String getStatusLabel() {
        if (status == ProductStatus.ON_SALE && !isOnSale()) {
            this.status = ProductStatus.SALE_EXPIRED;
        }
        return status != null ? status.getLabel() : "";
    }
    public String getTargetDescription(){ return target != null ? target.getDescription() : ""; }
    public String getLobLabel() {
        if (lineOfBusiness == LineOfBusiness.AUTO && target != null)
            return target.getAutoLabel();
        return lineOfBusiness != null ? lineOfBusiness.getLabel() : "";
    }

    // Setters
    public void setProductId(String v)               { this.productId = v; }
    public void setProductCode(String v)             { this.productCode = v; }
    public void setProductName(String v)             { this.productName = v; }
    public void setDescription(String v)             { this.description = v; }
    public void setSaleStartDate(Date v)             { this.saleStartDate = v; }
    public void setSaleEndDate(Date v)               { this.saleEndDate = v; }
    public void setStatus(ProductStatus v)           { this.status = v; }
    public void setTarget(Target v)                  { this.target = v; }
    public void setLineOfBusiness(LineOfBusiness v)  { this.lineOfBusiness = v; }
    public void setRiders(List<ProductRider> v)      { this.riders = v; }
    public void setDocuments(List<ProductDocument> v){ this.documents = v; }
    public void setCoverages(List<ProductCoverage> v){ this.coverages = v; }
    public void setCreatedAt(Date v)                 { this.createdAt = v; }

    // ── DAO 위임 ──────────────────────────────────────────────
    public static java.util.List<Product> findAll()          { return infra.dao.ProductDao.getInstance().findAll(); }
    public static Product findById(String productId)         { return infra.dao.ProductDao.getInstance().findById(productId); }
    public static boolean existsByCode(String code)          { return infra.dao.ProductDao.getInstance().existsByCode(code); }
    public void save()                                       { infra.dao.ProductDao.getInstance().save(this); }

    // Getters
    public List<ProductCoverage> getCoverages()  { return coverages; }
    public Date getCreatedAt()                   { return createdAt; }
    public String getDescription()               { return description; }
    public List<ProductDocument> getDocuments()  { return documents; }
    public LineOfBusiness getLineOfBusiness()    { return lineOfBusiness; }
    public String getProductCode()               { return productCode; }
    public String getProductId()                 { return productId; }
    public String getProductName()               { return productName; }
    public List<ProductRider> getRiders()        { return riders; }
    public Date getSaleEndDate()                 { return saleEndDate; }
    public Date getSaleStartDate()               { return saleStartDate; }
    public ProductStatus getStatus()             { return status; }
    public Target getTarget()                    { return target; }
}
