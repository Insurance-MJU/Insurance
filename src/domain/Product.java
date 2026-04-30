package domain;

import java.util.Date;
import java.util.List;

public class Product {
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
    private Status status;
    private Target target;

    public enum LineOfBusiness { AUTO, LIFE, FIRE }

    public enum Status { DESIGN, ON_SALE, DISCONTINUED }

    public enum Target { PERSONAL, BUSINESS, COMMERCIAL }

    public void discontinue() { this.status = Status.DISCONTINUED; }
    public void ondesign()    { this.status = Status.DESIGN; }
    public void onsale()      { this.status = Status.ON_SALE; }

    public boolean isOnSale() {
        if (status != Status.ON_SALE) return false;
        Date now = new Date();
        boolean afterStart = saleStartDate == null || !now.before(saleStartDate);
        boolean beforeEnd  = saleEndDate   == null || !now.after(saleEndDate);
        return afterStart && beforeEnd;
    }

    public String getTargetDescription() {
        if (target == Target.PERSONAL)   return "만 20세 이상 39세 이하 개인";
        if (target == Target.BUSINESS)   return "업무용 차량 보유 사업자";
        if (target == Target.COMMERCIAL) return "영업용 차량 보유자";
        return "";
    }

    public String getStatusLabel() {
        if (status == Status.ON_SALE)      return "판매중";
        if (status == Status.DISCONTINUED) return "판매중지";
        if (status == Status.DESIGN)       return "설계중";
        return "";
    }

    // Setters
    public void setProductId(String v)               { this.productId = v; }
    public void setProductCode(String v)             { this.productCode = v; }
    public void setProductName(String v)             { this.productName = v; }
    public void setDescription(String v)             { this.description = v; }
    public void setSaleStartDate(Date v)             { this.saleStartDate = v; }
    public void setSaleEndDate(Date v)               { this.saleEndDate = v; }
    public void setStatus(Status v)                  { this.status = v; }
    public void setTarget(Target v)                  { this.target = v; }
    public void setLineOfBusiness(LineOfBusiness v)  { this.lineOfBusiness = v; }
    public void setRiders(List<ProductRider> v)      { this.riders = v; }
    public void setDocuments(List<ProductDocument> v){ this.documents = v; }
    public void setCoverages(List<ProductCoverage> v){ this.coverages = v; }
    public void setCreatedAt(Date v)                 { this.createdAt = v; }

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
    public Status getStatus()                    { return status; }
    public Target getTarget()                    { return target; }
}
