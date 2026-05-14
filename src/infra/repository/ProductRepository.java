package infra.repository;

import domain.product.Product;
import domain.product.ProductDocument;
import domain.product.ProductRider;
import infra.util.FileStore;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProductRepository {
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

        // ── 1. 개인용 (판매중) ──────────────────────────────────
        Product personal = new Product();
        personal.setProductId("PROD-001");
        personal.setProductCode("CAR-2026-MZ-P");
        personal.setProductName("MZ세대 다이렉트 개인용자동차보험");
        personal.setDescription("MZ 세대를 위한 다이렉트 자동차보험입니다.");
        personal.setTarget(Product.Target.PERSONAL);
        personal.setLineOfBusiness(Product.LineOfBusiness.AUTO);
        personal.setStatus(Product.Status.ON_SALE);
        try {
            personal.setSaleStartDate(sdf.parse("2026-04-23"));
            personal.setSaleEndDate(sdf.parse("2026-10-01"));
        } catch (Exception ignored) {}

        ProductRider pr1 = new ProductRider();
        pr1.setProductRiderId("PR-001"); pr1.setProductId("PROD-001");
        pr1.setRiderCode("RC-MILEAGE"); pr1.setRiderId("RIDER-001");
        pr1.setRiderName("마일리지 특약");

        ProductRider pr2 = new ProductRider();
        pr2.setProductRiderId("PR-002"); pr2.setProductId("PROD-001");
        pr2.setRiderCode("RC-TMAP"); pr2.setRiderId("RIDER-002");
        pr2.setRiderName("티맵안전운전 할인특약");

        personal.setRiders(Arrays.asList(pr1, pr2));

        ProductDocument doc1 = new ProductDocument();
        doc1.setProductDocumentId("DOC-001"); doc1.setProductId("PROD-001");
        doc1.setDocType(ProductDocument.DocType.GENERAL_TERMS);
        doc1.setTitle("보통약관");
        doc1.setNote(
            "■ 보통약관\n\n" +
            "제1조 (보험계약의 성립)\n" +
            "  보험계약은 계약자가 청약하고 보험자가 승낙함으로써 성립합니다.\n\n" +
            "제2조 (보험기간)\n" +
            "  보험기간은 보험증권에 기재된 보험기간으로 합니다.\n\n" +
            "제3조 (보험금 지급 사유)\n" +
            "  보험사고로 인한 손해를 보상합니다.\n\n" +
            "제4조 (보험금 지급 제한)\n" +
            "  고의 사고, 음주운전, 무면허 운전 시 보험금을 지급하지 않습니다.\n"
        );

        ProductDocument doc2 = new ProductDocument();
        doc2.setProductDocumentId("DOC-002"); doc2.setProductId("PROD-001");
        doc2.setDocType(ProductDocument.DocType.SPECIAL_TERMS);
        doc2.setTitle("특별약관");
        doc2.setNote(
            "■ 특별약관\n\n" +
            "제1조 (마일리지 특약)\n" +
            "  연간 주행거리에 따라 보험료를 환급합니다.\n\n" +
            "제2조 (티맵안전운전 할인특약)\n" +
            "  티맵 안전운전 점수에 따라 보험료를 할인합니다.\n"
        );

        personal.setDocuments(new ArrayList<>(Arrays.asList(doc1, doc2)));
        STORE.add(personal);

        // ── 2. 업무용 (판매중) ──────────────────────────────────
        Product business = new Product();
        business.setProductId("PROD-002");
        business.setProductCode("CAR-2026-MZ-B");
        business.setProductName("MZ세대 다이렉트 업무용자동차보험");
        business.setDescription("업무용 차량을 위한 다이렉트 자동차보험입니다.");
        business.setTarget(Product.Target.BUSINESS);
        business.setLineOfBusiness(Product.LineOfBusiness.AUTO);
        business.setStatus(Product.Status.ON_SALE);
        try {
            business.setSaleStartDate(sdf.parse("2026-04-23"));
            business.setSaleEndDate(sdf.parse("2026-10-01"));
        } catch (Exception ignored) {}
        business.setRiders(new ArrayList<>());
        business.setDocuments(new ArrayList<>());
        STORE.add(business);

        // ── 3. 영업용 (판매중지 - E1 시나리오용) ──────────────
        Product commercial = new Product();
        commercial.setProductId("PROD-003");
        commercial.setProductCode("CAR-2026-MZ-C");
        commercial.setProductName("MZ세대 다이렉트 영업용자동차보험");
        commercial.setDescription("영업용 차량을 위한 다이렉트 자동차보험입니다.");
        commercial.setTarget(Product.Target.COMMERCIAL);
        commercial.setLineOfBusiness(Product.LineOfBusiness.AUTO);
        commercial.setStatus(Product.Status.DISCONTINUED);
        try {
            commercial.setSaleStartDate(sdf.parse("2026-01-01"));
            commercial.setSaleEndDate(sdf.parse("2026-04-01"));
        } catch (Exception ignored) {}
        commercial.setRiders(new ArrayList<>());
        commercial.setDocuments(new ArrayList<>());
        STORE.add(commercial);

        // ── 4. 설계완료 상품 (CT 유즈케이스 데모용) ──────────────
        Product design = new Product();
        design.setProductId("PROD-004");
        design.setProductCode("CAR-2026-MZ");
        design.setProductName("MZ 세대 다이렉트 차보험");
        design.setDescription("MZ 세대를 위한 신규 설계 자동차보험.");
        design.setTarget(Product.Target.PERSONAL);
        design.setLineOfBusiness(Product.LineOfBusiness.AUTO);
        design.setStatus(Product.Status.DESIGN_COMPLETE);
        try {
            design.setSaleStartDate(sdf.parse("2026-05-01"));
            design.setSaleEndDate(sdf.parse("2027-04-30"));
        } catch (Exception ignored) {}
        design.setRiders(new ArrayList<>());
        design.setDocuments(new ArrayList<>());
        STORE.add(design);

        FileStore.save("products.dat", STORE);
    }

    public List<Product> findAll() {
        return Collections.unmodifiableList(STORE);
    }

    public Product findById(String productId) {
        return STORE.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst().orElse(null);
    }

    public boolean existsByCode(String code) {
        return STORE.stream().anyMatch(p -> code.equals(p.getProductCode()));
    }

    public void save(Product p) {
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
}
