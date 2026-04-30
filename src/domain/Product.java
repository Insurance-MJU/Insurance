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

    public enum LineOfBusiness {}
    public enum Status {}
    public enum Target {}

    public void discontinue() {}
    public void ondesign() {}
    public void onsale() {}

    public List<ProductCoverage> getCoverages() { return coverages; }
    public Date getCreatedAt() { return createdAt; }
    public String getDescription() { return description; }
    public List<ProductDocument> getDocuments() { return documents; }
    public LineOfBusiness getLineOfBusiness() { return lineOfBusiness; }
    public String getProductCode() { return productCode; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public List<ProductRider> getRiders() { return riders; }
    public Date getSaleEndDate() { return saleEndDate; }
    public Date getSaleStartDate() { return saleStartDate; }
    public Status getStatus() { return status; }
    public Target getTarget() { return target; }
}
