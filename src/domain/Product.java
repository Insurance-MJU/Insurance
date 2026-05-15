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
    public void applySalePermit()  { this.status = Status.SALE_PENDING; }

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

    // ── 영속성 ────────────────────────────────────────────────
    private static final List<Product> STORE;
    static {
        List<Product> loaded = FileStore.load("products.dat");
        if (loaded != null) { STORE = loaded; }
        else { STORE = new ArrayList<>(); initDefaults(); }
    }
    private static void initDefaults() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Product personal = new Product();
        personal.setProductId("PROD-001"); personal.setProductCode("CAR-2026-MZ-P");
        personal.setProductName("MZ세대 다이렉트 개인용자동차보험");
        personal.setDescription("MZ 세대를 위한 다이렉트 자동차보험입니다.");
        personal.setTarget(Target.PERSONAL); personal.setLineOfBusiness(LineOfBusiness.AUTO);
        personal.setStatus(Status.ON_SALE);
        try { personal.setSaleStartDate(sdf.parse("2026-04-23")); personal.setSaleEndDate(sdf.parse("2026-10-01")); } catch (Exception ignored) {}
        ProductRider pr1 = new ProductRider(); pr1.setProductRiderId("PR-001"); pr1.setProductId("PROD-001"); pr1.setRiderCode("RC-MILEAGE"); pr1.setRiderId("RIDER-001"); pr1.setRiderName("마일리지 특약");
        ProductRider pr2 = new ProductRider(); pr2.setProductRiderId("PR-002"); pr2.setProductId("PROD-001"); pr2.setRiderCode("RC-TMAP"); pr2.setRiderId("RIDER-002"); pr2.setRiderName("티맵안전운전 할인특약");
        personal.setRiders(Arrays.asList(pr1, pr2));
        ProductDocument doc1 = new ProductDocument(); doc1.setProductDocumentId("DOC-001"); doc1.setProductId("PROD-001"); doc1.setDocType(ProductDocument.DocType.GENERAL_TERMS); doc1.setTitle("보통약관");
        doc1.setNote("■ 보통약관\n\n제1조 (보험계약의 성립)\n  보험계약은 계약자가 청약하고 보험자가 승낙함으로써 성립합니다.\n\n제2조 (보험기간)\n  보험기간은 보험증권에 기재된 보험기간으로 합니다.\n\n제3조 (보험금 지급 사유)\n  보험사고로 인한 손해를 보상합니다.\n\n제4조 (보험금 지급 제한)\n  고의 사고, 음주운전, 무면허 운전 시 보험금을 지급하지 않습니다.\n");
        ProductDocument doc2 = new ProductDocument(); doc2.setProductDocumentId("DOC-002"); doc2.setProductId("PROD-001"); doc2.setDocType(ProductDocument.DocType.SPECIAL_TERMS); doc2.setTitle("특별약관");
        doc2.setNote("■ 특별약관\n\n제1조 (마일리지 특약)\n  연간 주행거리에 따라 보험료를 환급합니다.\n\n제2조 (티맵안전운전 할인특약)\n  티맵 안전운전 점수에 따라 보험료를 할인합니다.\n");
        personal.setDocuments(new ArrayList<>(Arrays.asList(doc1, doc2)));
        STORE.add(personal);

        Product business = new Product();
        business.setProductId("PROD-002"); business.setProductCode("CAR-2026-MZ-B");
        business.setProductName("MZ세대 다이렉트 업무용자동차보험");
        business.setDescription("업무용 차량을 위한 다이렉트 자동차보험입니다.");
        business.setTarget(Target.BUSINESS); business.setLineOfBusiness(LineOfBusiness.AUTO);
        business.setStatus(Status.ON_SALE);
        try { business.setSaleStartDate(sdf.parse("2026-04-23")); business.setSaleEndDate(sdf.parse("2026-10-01")); } catch (Exception ignored) {}
        business.setRiders(new ArrayList<>()); business.setDocuments(new ArrayList<>());
        STORE.add(business);

        Product commercial = new Product();
        commercial.setProductId("PROD-003"); commercial.setProductCode("CAR-2026-MZ-C");
        commercial.setProductName("MZ세대 다이렉트 영업용자동차보험");
        commercial.setDescription("영업용 차량을 위한 다이렉트 자동차보험입니다.");
        commercial.setTarget(Target.COMMERCIAL); commercial.setLineOfBusiness(LineOfBusiness.AUTO);
        commercial.setStatus(Status.DISCONTINUED);
        try { commercial.setSaleStartDate(sdf.parse("2026-01-01")); commercial.setSaleEndDate(sdf.parse("2026-04-01")); } catch (Exception ignored) {}
        commercial.setRiders(new ArrayList<>()); commercial.setDocuments(new ArrayList<>());
        STORE.add(commercial);

        Product design = new Product();
        design.setProductId("PROD-004"); design.setProductCode("CAR-2026-MZ");
        design.setProductName("MZ 세대 다이렉트 차보험");
        design.setDescription("MZ 세대를 위한 신규 설계 자동차보험.");
        design.setTarget(Target.PERSONAL); design.setLineOfBusiness(LineOfBusiness.AUTO);
        design.setStatus(Status.DESIGN_COMPLETE);
        try { design.setSaleStartDate(sdf.parse("2026-05-01")); design.setSaleEndDate(sdf.parse("2027-04-30")); } catch (Exception ignored) {}
        design.setRiders(new ArrayList<>()); design.setDocuments(new ArrayList<>());
        STORE.add(design);

        FileStore.save("products.dat", STORE);
    }

    public static List<Product> findAll() { return Collections.unmodifiableList(STORE); }
    public static Product findById(String productId) {
        return STORE.stream().filter(p -> p.productId.equals(productId)).findFirst().orElse(null);
    }
    public static boolean existsByCode(String code) {
        return STORE.stream().anyMatch(p -> code.equals(p.productCode));
    }
    public void save() {
        for (int i = 0; i < STORE.size(); i++) {
            if (STORE.get(i).productId.equals(this.productId)) {
                STORE.set(i, this);
                FileStore.save("products.dat", STORE);
                return;
            }
        }
        STORE.add(this);
        FileStore.save("products.dat", STORE);
    }
}
