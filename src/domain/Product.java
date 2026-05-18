package domain;

import infra.util.FileStore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    // ── 정적 저장소 ───────────────────────────────────────────
    private static final List<Product> STORE;

    static {
        List<Product> loaded = FileStore.load("products.dat");
        if (loaded != null) {
            STORE = loaded;
        } else {
            STORE = new ArrayList<>();
            initDefaults();
        }
    }

    private static void initDefaults() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Product personal = new Product();
        personal.productId = "PROD-001"; personal.productCode = "CAR-2026-MZ-P";
        personal.productName = "MZ세대 다이렉트 개인용자동차보험";
        personal.description = "MZ 세대를 위한 다이렉트 자동차보험입니다.";
        personal.target = Target.PERSONAL; personal.lineOfBusiness = LineOfBusiness.AUTO;
        personal.status = Status.ON_SALE; personal.createdAt = new Date();
        personal.riders = new ArrayList<>(Arrays.asList(
            rider("PR-001", "PROD-001", "RC-MILEAGE", "RIDER-001", "마일리지 특약", null),
            rider("PR-002", "PROD-001", "RC-TMAP",    "RIDER-002", "티맵안전운전 할인특약", null)
        ));
        personal.documents = new ArrayList<>(); personal.coverages = new ArrayList<>();
        try { personal.saleStartDate = sdf.parse("2026-04-23"); personal.saleEndDate = sdf.parse("2026-10-01"); } catch (Exception ignored) {}
        STORE.add(personal);

        Product business = new Product();
        business.productId = "PROD-002"; business.productCode = "CAR-2026-MZ-B";
        business.productName = "MZ세대 다이렉트 업무용자동차보험";
        business.description = "업무용 차량을 위한 다이렉트 자동차보험입니다.";
        business.target = Target.BUSINESS; business.lineOfBusiness = LineOfBusiness.AUTO;
        business.status = Status.ON_SALE; business.createdAt = new Date();
        business.riders = new ArrayList<>(); business.documents = new ArrayList<>(); business.coverages = new ArrayList<>();
        try { business.saleStartDate = sdf.parse("2026-04-23"); business.saleEndDate = sdf.parse("2026-10-01"); } catch (Exception ignored) {}
        STORE.add(business);

        Product commercial = new Product();
        commercial.productId = "PROD-003"; commercial.productCode = "CAR-2026-MZ-C";
        commercial.productName = "MZ세대 다이렉트 영업용자동차보험";
        commercial.description = "영업용 차량을 위한 다이렉트 자동차보험입니다.";
        commercial.target = Target.COMMERCIAL; commercial.lineOfBusiness = LineOfBusiness.AUTO;
        commercial.status = Status.DISCONTINUED; commercial.createdAt = new Date();
        commercial.riders = new ArrayList<>(); commercial.documents = new ArrayList<>(); commercial.coverages = new ArrayList<>();
        try { commercial.saleStartDate = sdf.parse("2026-01-01"); commercial.saleEndDate = sdf.parse("2026-04-01"); } catch (Exception ignored) {}
        STORE.add(commercial);

        Product design = new Product();
        design.productId = "PROD-004"; design.productCode = "CAR-2026-MZ";
        design.productName = "MZ 세대 다이렉트 차보험";
        design.description = "MZ 세대를 위한 신규 설계 자동차보험.";
        design.target = Target.PERSONAL; design.lineOfBusiness = LineOfBusiness.AUTO;
        design.status = Status.DESIGN_COMPLETE; design.createdAt = new Date();
        design.riders = new ArrayList<>(); design.documents = new ArrayList<>(); design.coverages = new ArrayList<>();
        try { design.saleStartDate = sdf.parse("2026-05-01"); design.saleEndDate = sdf.parse("2027-04-30"); } catch (Exception ignored) {}
        STORE.add(design);

        FileStore.save("products.dat", STORE);
    }

    private static ProductRider rider(String prId, String prodId, String code, String riderId, String name, Double rate) {
        ProductRider pr = new ProductRider();
        pr.setProductRiderId(prId); pr.setProductId(prodId);
        pr.setRiderCode(code); pr.setRiderId(riderId);
        pr.setRiderName(name); pr.setDiscountRate(rate);
        return pr;
    }

    public static List<Product> findAll() {
        return Collections.unmodifiableList(STORE);
    }

    public static Product findById(String productId) {
        return STORE.stream()
            .filter(p -> p.getProductId().equals(productId))
            .findFirst().orElse(null);
    }

    public static boolean existsByCode(String code) {
        return STORE.stream().anyMatch(p -> code.equals(p.getProductCode()));
    }

    public static void save(Product p) {
        for (int i = 0; i < STORE.size(); i++) {
            if (STORE.get(i).getProductId().equals(p.getProductId())) {
                STORE.set(i, p);
                FileStore.save("products.dat", STORE);
                return;
            }
        }
        STORE.add(p);
        FileStore.save("products.dat", STORE);
    }
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

    public enum LineOfBusiness {
        AUTO("자동차보험"), LIFE("생명보험"), FIRE("화재보험");
        private final String label;
        LineOfBusiness(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Status {
        DESIGN("설계중"), DESIGN_COMPLETE("설계완료"),
        APPROVAL_PENDING("인가신청중"), APPROVED("인가완료"),
        SALE_PENDING("판매신청중"), ON_SALE("판매중"), DISCONTINUED("판매중지");
        private final String label;
        Status(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Target {
        PERSONAL("만 20세 이상 39세 이하 개인", "개인용"),
        BUSINESS("업무용 차량 보유 사업자", "업무용"),
        COMMERCIAL("영업용 차량 보유자", "영업용");
        private final String description;
        private final String autoPrefix;
        Target(String description, String autoPrefix) {
            this.description = description;
            this.autoPrefix  = autoPrefix;
        }
        public String getDescription() { return description; }
        public String getAutoLabel()   { return autoPrefix + "자동차보험"; }
    }

    // ── 정적 팩토리: 신규 상품 설계 ──────────────────────────
    public static Product design(String productCode, String productName, String description,
                                  Target target, Date saleStart, Date saleEnd) {
        if (existsByCode(productCode))
            throw new IllegalArgumentException("이미 사용 중인 상품코드입니다: " + productCode);
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

    public void discontinue()      { this.status = Status.DISCONTINUED; }
    public void ondesign()         { this.status = Status.DESIGN; }
    public void onsale()           { this.status = Status.ON_SALE; }
    public void completeDesign()   { this.status = Status.DESIGN_COMPLETE; }
    public void applyForApproval() { this.status = Status.APPROVAL_PENDING; }
    public void completeApproval() { this.status = Status.APPROVED; }
    public void rejectApproval()   { this.status = Status.DESIGN_COMPLETE; }  // 인가 거절 → 재설계
    public void applySalePermit()  { this.status = Status.SALE_PENDING; }
    public void rejectSale()       { this.status = Status.APPROVED; }         // 판매 거절 → 인가완료로 복귀

    /** 자동차보험 표준 담보 목록 문자열 — 의무 담보(대인I) 포함 전 담보 */
    public String getDefaultCoverageDescription() {
        return "대인배상I, 대인배상II, 대물배상, 자동차상해, 무보험차상해, 자기차량손해";
    }

    public boolean isOnSale() {
        if (status != Status.ON_SALE) return false;
        Date now = new Date();
        boolean afterStart = saleStartDate == null || !now.before(saleStartDate);
        boolean beforeEnd  = saleEndDate   == null || !now.after(saleEndDate);
        return afterStart && beforeEnd;
    }

    public String getStatusLabel()      { return status != null ? status.getLabel() : ""; }
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
