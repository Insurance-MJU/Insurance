package infra.dao;

import domain.*;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import domain.ProductList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private final Database db;

    public ProductDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Product> EXTRACTOR = rs -> mapRow(rs);

    private static Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getString("product_id"));
        p.setProductCode(rs.getString("product_code"));
        p.setProductName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));
        String lob = rs.getString("line_of_business");
        if (lob != null) p.setLineOfBusiness(LineOfBusiness.valueOf(lob));
        Timestamp startTs = rs.getTimestamp("sale_start_date");
        if (startTs != null) p.setSaleStartDate(new java.util.Date(startTs.getTime()));
        Timestamp endTs = rs.getTimestamp("sale_end_date");
        if (endTs != null) p.setSaleEndDate(new java.util.Date(endTs.getTime()));
        String statusStr = rs.getString("status");
        if (statusStr != null) p.setStatus(ProductStatus.valueOf(statusStr));
        String targetStr = rs.getString("target");
        if (targetStr != null) p.setTarget(Target.valueOf(targetStr));
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) p.setCreatedAt(new java.util.Date(createdTs.getTime()));
        return p;
    }

    private static final ResultSetExtractor<ProductCoverage> COV_EXTRACTOR = rs -> {
        ProductCoverage pc = new ProductCoverage();
        pc.setProductCoverageId(rs.getString("product_coverage_id"));
        pc.setProductId(rs.getString("product_id"));
        pc.setCoverageMasterId(rs.getString("coverage_master_id"));
        pc.setCoverageName(rs.getString("coverage_name"));
        pc.setMandatory(rs.getInt("mandatory") == 1);
        return pc;
    };

    private static final ResultSetExtractor<ProductRider> RIDER_EXTRACTOR = rs -> {
        ProductRider pr = new ProductRider();
        pr.setProductRiderId(rs.getString("product_rider_id"));
        pr.setProductId(rs.getString("product_id"));
        pr.setRiderId(rs.getString("rider_id"));
        pr.setRiderCode(rs.getString("rider_code"));
        pr.setRiderName(rs.getString("rider_name"));
        pr.setDiscountRate(rs.getDouble("discount_rate"));
        return pr;
    };

    private static final ResultSetExtractor<ProductDocument> DOC_EXTRACTOR = rs -> {
        ProductDocument doc = new ProductDocument();
        doc.setProductDocumentId(rs.getString("product_document_id"));
        doc.setProductId(rs.getString("product_id"));
        String docType = rs.getString("doc_type");
        if (docType != null) doc.setDocType(ProductDocument.DocType.valueOf(docType));
        doc.setTitle(rs.getString("title"));
        doc.setNote(rs.getString("note"));
        doc.setFilename(rs.getString("filename"));
        doc.setFilePath(rs.getString("file_path"));
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) doc.setCreatedAt(new java.util.Date(createdTs.getTime()));
        Timestamp submittedTs = rs.getTimestamp("submitted_at");
        if (submittedTs != null) doc.setSubmittedAt(new java.util.Date(submittedTs.getTime()));
        Timestamp receivedTs = rs.getTimestamp("received_at");
        if (receivedTs != null) doc.setReceivedAt(new java.util.Date(receivedTs.getTime()));
        return doc;
    };

    private Product loadFull(Product p) {
        if (p == null) return null;
        p.setCoverages(db.queryForList(
            "SELECT * FROM product_coverages WHERE product_id = ?", COV_EXTRACTOR, p.getProductId()));
        p.setRiders(db.queryForList(
            "SELECT * FROM product_riders WHERE product_id = ?", RIDER_EXTRACTOR, p.getProductId()));
        p.setDocuments(db.queryForList(
            "SELECT * FROM product_documents WHERE product_id = ?", DOC_EXTRACTOR, p.getProductId()));
        return p;
    }

    public ProductList findAll() {
        List<Product> list = db.queryForList("SELECT * FROM products", EXTRACTOR);
        list.forEach(this::loadFull);
        return new ProductList(list);
    }

    public Product findById(String productId) {
        Product p = db.queryForObject(
            "SELECT * FROM products WHERE product_id = ?", EXTRACTOR, productId);
        return loadFull(p);
    }

    public boolean existsByCode(String code) {
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM products WHERE product_code = ?",
            rs -> rs.getInt(1), code);
        return count != null && count > 0;
    }

    public void save(Product p) {
        db.execute(
            "INSERT INTO products (product_id, product_code, product_name, description," +
            " line_of_business, sale_start_date, sale_end_date, status, target, created_at)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " product_code=VALUES(product_code), product_name=VALUES(product_name)," +
            " description=VALUES(description), line_of_business=VALUES(line_of_business)," +
            " sale_start_date=VALUES(sale_start_date), sale_end_date=VALUES(sale_end_date)," +
            " status=VALUES(status), target=VALUES(target), created_at=VALUES(created_at)",
            p.getProductId(),
            p.getProductCode(),
            p.getProductName(),
            p.getDescription(),
            p.getLineOfBusiness() != null ? p.getLineOfBusiness().name() : null,
            p.getSaleStartDate() != null ? new Timestamp(p.getSaleStartDate().getTime()) : null,
            p.getSaleEndDate() != null ? new Timestamp(p.getSaleEndDate().getTime()) : null,
            p.getStatus() != null ? p.getStatus().name() : null,
            p.getTarget() != null ? p.getTarget().name() : null,
            p.getCreatedAt() != null ? new Timestamp(p.getCreatedAt().getTime()) : null
        );

        // Save sub-tables: delete + reinsert
        if (p.getCoverages() != null) {
            db.execute("DELETE FROM product_coverages WHERE product_id = ?", p.getProductId());
            for (ProductCoverage pc : p.getCoverages()) {
                String pcId = pc.getProductCoverageId() != null ? pc.getProductCoverageId()
                    : "PC-" + p.getProductId() + "-" + pc.getCoverageMasterId();
                db.execute(
                    "INSERT INTO product_coverages (product_coverage_id, product_id, coverage_master_id," +
                    " coverage_name, coverage_type, mandatory, limit_amount) VALUES (?,?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE coverage_name=VALUES(coverage_name)," +
                    " mandatory=VALUES(mandatory)",
                    pcId, p.getProductId(), pc.getCoverageMasterId(), pc.getCoverageName(),
                    pc.getCoverageType() != null ? pc.getCoverageType().name() : null,
                    pc.isMandatory() ? 1 : 0,
                    0L
                );
            }
        }

        if (p.getRiders() != null) {
            db.execute("DELETE FROM product_riders WHERE product_id = ?", p.getProductId());
            for (ProductRider pr : p.getRiders()) {
                String prId = pr.getProductRiderId() != null ? pr.getProductRiderId()
                    : "PR-" + p.getProductId() + "-" + pr.getRiderId();
                db.execute(
                    "INSERT INTO product_riders (product_rider_id, product_id, rider_id, rider_code, rider_name, discount_rate)" +
                    " VALUES (?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE rider_name=VALUES(rider_name), discount_rate=VALUES(discount_rate)",
                    prId, p.getProductId(), pr.getRiderId(), pr.getRiderCode(), pr.getRiderName(),
                    pr.getDiscountRate() != null ? pr.getDiscountRate() : 0.0
                );
            }
        }

        if (p.getDocuments() != null) {
            db.execute("DELETE FROM product_documents WHERE product_id = ?", p.getProductId());
            for (ProductDocument doc : p.getDocuments()) {
                String docId = doc.getProductDocumentId() != null ? doc.getProductDocumentId()
                    : "DOC-" + System.nanoTime();
                db.execute(
                    "INSERT INTO product_documents (product_document_id, product_id, doc_type, title, note, filename, file_path," +
                    " created_at, submitted_at, received_at) VALUES (?,?,?,?,?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE title=VALUES(title), note=VALUES(note), filename=VALUES(filename)," +
                    " file_path=VALUES(file_path), submitted_at=VALUES(submitted_at), received_at=VALUES(received_at)",
                    docId, p.getProductId(),
                    doc.getDocType() != null ? doc.getDocType().name() : null,
                    doc.getTitle(), doc.getNote(), doc.getFilename(), doc.getFilePath(),
                    doc.getCreatedAt() != null ? new Timestamp(doc.getCreatedAt().getTime()) : null,
                    doc.getSubmittedAt() != null ? new Timestamp(doc.getSubmittedAt().getTime()) : null,
                    doc.getReceivedAt() != null ? new Timestamp(doc.getReceivedAt().getTime()) : null
                );
            }
        }
    }
}
