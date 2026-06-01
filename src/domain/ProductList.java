package domain;

import common.exception.domain.NotFoundException;
import infra.dao.ProductDao;
import infra.vo.ProductCoverageVO;
import infra.vo.ProductDocumentVO;
import infra.vo.ProductRiderVO;
import infra.vo.ProductVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductList {
    private final ProductDao dao;
    private final List<Product> products;

    public ProductList(ProductDao dao) {
        this.dao      = dao;
        this.products = Collections.emptyList();
    }

    public ProductList(List<Product> products) {
        this.dao      = null;
        this.products = Collections.unmodifiableList(products);
    }

    private static Product toDomain(ProductVO vo) {
        if (vo == null) return null;
        Product p = new Product();
        p.setProductId(vo.productId);
        p.setProductCode(vo.productCode);
        p.setProductName(vo.productName);
        p.setDescription(vo.description);
        if (vo.lineOfBusiness != null) p.setLineOfBusiness(LineOfBusiness.valueOf(vo.lineOfBusiness));
        p.setSaleStartDate(vo.saleStartDate);
        p.setSaleEndDate(vo.saleEndDate);
        if (vo.status != null) p.setStatus(ProductStatus.valueOf(vo.status));
        if (vo.target != null) p.setTarget(Target.valueOf(vo.target));
        p.setCreatedAt(vo.createdAt);
        if (vo.coverages != null) p.setCoverages(vo.coverages.stream().map(ProductList::toCoverage).collect(Collectors.toList()));
        if (vo.riders != null)   p.setRiders(vo.riders.stream().map(ProductList::toRider).collect(Collectors.toList()));
        if (vo.documents != null) p.setDocuments(vo.documents.stream().map(ProductList::toDocument).collect(Collectors.toList()));
        return p;
    }

    private static ProductCoverage toCoverage(ProductCoverageVO vo) {
        ProductCoverage pc = new ProductCoverage();
        pc.setProductCoverageId(vo.productCoverageId);
        pc.setProductId(vo.productId);
        pc.setCoverageMasterId(vo.coverageMasterId);
        pc.setCoverageName(vo.coverageName);
        if (vo.coverageType != null) pc.setCoverageType(CoverageType.valueOf(vo.coverageType));
        pc.setMandatory(vo.mandatory);
        return pc;
    }

    private static ProductRider toRider(ProductRiderVO vo) {
        ProductRider pr = new ProductRider();
        pr.setProductRiderId(vo.productRiderId);
        pr.setProductId(vo.productId);
        pr.setRiderId(vo.riderId);
        pr.setRiderCode(vo.riderCode);
        pr.setRiderName(vo.riderName);
        pr.setDiscountRate(vo.discountRate);
        return pr;
    }

    private static ProductDocument toDocument(ProductDocumentVO vo) {
        ProductDocument doc = new ProductDocument();
        doc.setProductDocumentId(vo.productDocumentId);
        doc.setProductId(vo.productId);
        if (vo.docType != null) doc.setDocType(ProductDocument.DocType.valueOf(vo.docType));
        doc.setTitle(vo.title);
        doc.setNote(vo.note);
        doc.setFilename(vo.filename);
        doc.setFilePath(vo.filePath);
        doc.setCreatedAt(vo.createdAt);
        doc.setSubmittedAt(vo.submittedAt);
        doc.setReceivedAt(vo.receivedAt);
        return doc;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public ProductList findAll() {
        return new ProductList(dao.findAll().stream().map(ProductList::toDomain).collect(Collectors.toList()));
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Product> getAll() { return products; }
    public boolean isEmpty() { return products.isEmpty(); }
    public int size() { return products.size(); }
    public Product get(int index) { return products.get(index); }

    public ProductList onSaleOnly() {
        return new ProductList(
            products.stream()
                .filter(Product::isOnSale)
                .collect(Collectors.toList())
        );
    }

    public Product findById(String productId) {
        if (!products.isEmpty()) {
            return products.stream()
                .filter(p -> productId.equals(p.getProductId()))
                .findFirst()
                .orElse(null);
        }
        return toDomain(dao.findById(productId));
    }

    public Product getById(String productId) {
        Product p = findById(productId);
        if (p == null) throw new NotFoundException("상품을 찾을 수 없습니다: " + productId);
        return p;
    }

    public boolean existsByCode(String code) {
        return dao.existsByCode(code);
    }

    public boolean existsById(String productId) {
        return findById(productId) != null;
    }

    public void validateExists(String productId) {
        if (!existsById(productId)) throw new NotFoundException("상품을 찾을 수 없습니다: " + productId);
    }

    public void delete(String productId) {
        dao.delete(productId);
    }

    public void save(Product product) {
        List<ProductCoverageVO> covVOs = product.getCoverages() != null
            ? product.getCoverages().stream().map(pc -> new ProductCoverageVO(
                pc.getProductCoverageId(), pc.getProductId(), pc.getCoverageMasterId(),
                pc.getCoverageName(),
                pc.getCoverageType() != null ? pc.getCoverageType().name() : null,
                pc.isMandatory())).collect(Collectors.toList()) : null;
        List<ProductRiderVO> riderVOs = product.getRiders() != null
            ? product.getRiders().stream().map(pr -> new ProductRiderVO(
                pr.getProductRiderId(), pr.getProductId(), pr.getRiderId(),
                pr.getRiderCode(), pr.getRiderName(),
                pr.getDiscountRate() != null ? pr.getDiscountRate() : 0.0)).collect(Collectors.toList()) : null;
        List<ProductDocumentVO> docVOs = product.getDocuments() != null
            ? product.getDocuments().stream().map(doc -> new ProductDocumentVO(
                doc.getProductDocumentId(), doc.getProductId(),
                doc.getDocType() != null ? doc.getDocType().name() : null,
                doc.getTitle(), doc.getNote(), doc.getFilename(), doc.getFilePath(),
                doc.getCreatedAt(), doc.getSubmittedAt(), doc.getReceivedAt())).collect(Collectors.toList()) : null;
        dao.save(new ProductVO(
            product.getProductId(), product.getProductCode(), product.getProductName(),
            product.getDescription(),
            product.getLineOfBusiness() != null ? product.getLineOfBusiness().name() : null,
            product.getSaleStartDate(), product.getSaleEndDate(),
            product.getStatus() != null ? product.getStatus().name() : null,
            product.getTarget() != null ? product.getTarget().name() : null,
            product.getCreatedAt(), covVOs, riderVOs, docVOs
        ));
    }
}
