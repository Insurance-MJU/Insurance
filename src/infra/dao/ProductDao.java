package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.ProductCoverageVO;
import infra.vo.ProductDocumentVO;
import infra.vo.ProductRiderVO;
import infra.vo.ProductVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private final Database db;

    public ProductDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<ProductVO> EXTRACTOR = rs -> mapRow(rs);

    private static ProductVO mapRow(ResultSet rs) throws SQLException {
        Timestamp startTs   = rs.getTimestamp("sale_start_date");
        Timestamp endTs     = rs.getTimestamp("sale_end_date");
        Timestamp createdTs = rs.getTimestamp("created_at");
        return new ProductVO(
            rs.getString("product_id"), rs.getString("product_code"),
            rs.getString("product_name"), rs.getString("description"),
            rs.getString("line_of_business"),
            startTs   != null ? new java.util.Date(startTs.getTime())   : null,
            endTs     != null ? new java.util.Date(endTs.getTime())     : null,
            rs.getString("status"), rs.getString("target"),
            createdTs != null ? new java.util.Date(createdTs.getTime()) : null,
            null, null, null
        );
    }

    private static final ResultSetExtractor<ProductCoverageVO> COV_EXTRACTOR = rs ->
        new ProductCoverageVO(
            rs.getString("product_coverage_id"), rs.getString("product_id"),
            rs.getString("coverage_master_id"),  rs.getString("coverage_name"),
            rs.getString("coverage_type"),       rs.getInt("mandatory") == 1
        );

    private static final ResultSetExtractor<ProductRiderVO> RIDER_EXTRACTOR = rs ->
        new ProductRiderVO(
            rs.getString("product_rider_id"), rs.getString("product_id"),
            rs.getString("rider_id"),         rs.getString("rider_code"),
            rs.getString("rider_name"),       rs.getDouble("discount_rate")
        );

    private static final ResultSetExtractor<ProductDocumentVO> DOC_EXTRACTOR = rs -> {
        Timestamp c = rs.getTimestamp("created_at");
        Timestamp s = rs.getTimestamp("submitted_at");
        Timestamp r = rs.getTimestamp("received_at");
        return new ProductDocumentVO(
            rs.getString("product_document_id"), rs.getString("product_id"),
            rs.getString("doc_type"),            rs.getString("title"),
            rs.getString("note"),                rs.getString("filename"),
            rs.getString("file_path"),
            c != null ? new java.util.Date(c.getTime()) : null,
            s != null ? new java.util.Date(s.getTime()) : null,
            r != null ? new java.util.Date(r.getTime()) : null
        );
    };

    private ProductVO loadFull(ProductVO vo) {
        if (vo == null) return null;
        return new ProductVO(
            vo.productId, vo.productCode, vo.productName, vo.description,
            vo.lineOfBusiness, vo.saleStartDate, vo.saleEndDate, vo.status,
            vo.target, vo.createdAt,
            db.queryForList("SELECT * FROM product_coverages WHERE product_id = ?", COV_EXTRACTOR,  vo.productId),
            db.queryForList("SELECT * FROM product_riders   WHERE product_id = ?", RIDER_EXTRACTOR, vo.productId),
            db.queryForList("SELECT * FROM product_documents WHERE product_id = ?", DOC_EXTRACTOR,  vo.productId)
        );
    }

    public List<ProductVO> findAll() {
        List<ProductVO> list = db.queryForList("SELECT * FROM products", EXTRACTOR);
        List<ProductVO> result = new ArrayList<>();
        for (ProductVO vo : list) result.add(loadFull(vo));
        return result;
    }

    public ProductVO findById(String productId) {
        return loadFull(db.queryForObject(
            "SELECT * FROM products WHERE product_id = ?", EXTRACTOR, productId));
    }

    public boolean existsByCode(String code) {
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM products WHERE product_code = ?", rs -> rs.getInt(1), code);
        return count != null && count > 0;
    }

    public void save(ProductVO vo) {
        db.execute(
            "INSERT INTO products (product_id, product_code, product_name, description," +
            " line_of_business, sale_start_date, sale_end_date, status, target, created_at)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " product_code=VALUES(product_code), product_name=VALUES(product_name)," +
            " description=VALUES(description), line_of_business=VALUES(line_of_business)," +
            " sale_start_date=VALUES(sale_start_date), sale_end_date=VALUES(sale_end_date)," +
            " status=VALUES(status), target=VALUES(target), created_at=VALUES(created_at)",
            vo.productId, vo.productCode, vo.productName, vo.description, vo.lineOfBusiness,
            vo.saleStartDate  != null ? new Timestamp(vo.saleStartDate.getTime())  : null,
            vo.saleEndDate    != null ? new Timestamp(vo.saleEndDate.getTime())    : null,
            vo.status, vo.target,
            vo.createdAt      != null ? new Timestamp(vo.createdAt.getTime())      : null
        );

        if (vo.coverages != null) {
            db.execute("DELETE FROM product_coverages WHERE product_id = ?", vo.productId);
            for (ProductCoverageVO pc : vo.coverages) {
                String pcId = pc.productCoverageId != null ? pc.productCoverageId
                    : "PC-" + vo.productId + "-" + pc.coverageMasterId;
                db.execute(
                    "INSERT INTO product_coverages (product_coverage_id, product_id, coverage_master_id," +
                    " coverage_name, coverage_type, mandatory, limit_amount) VALUES (?,?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE coverage_name=VALUES(coverage_name), mandatory=VALUES(mandatory)",
                    pcId, vo.productId, pc.coverageMasterId, pc.coverageName,
                    pc.coverageType, pc.mandatory ? 1 : 0, 0L
                );
            }
        }

        if (vo.riders != null) {
            db.execute("DELETE FROM product_riders WHERE product_id = ?", vo.productId);
            for (ProductRiderVO pr : vo.riders) {
                String prId = pr.productRiderId != null ? pr.productRiderId
                    : "PR-" + vo.productId + "-" + pr.riderId;
                db.execute(
                    "INSERT INTO product_riders (product_rider_id, product_id, rider_id, rider_code, rider_name, discount_rate)" +
                    " VALUES (?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE rider_name=VALUES(rider_name), discount_rate=VALUES(discount_rate)",
                    prId, vo.productId, pr.riderId, pr.riderCode, pr.riderName, pr.discountRate
                );
            }
        }

        if (vo.documents != null) {
            db.execute("DELETE FROM product_documents WHERE product_id = ?", vo.productId);
            for (ProductDocumentVO doc : vo.documents) {
                String docId = doc.productDocumentId != null ? doc.productDocumentId
                    : "DOC-" + System.nanoTime();
                db.execute(
                    "INSERT INTO product_documents (product_document_id, product_id, doc_type, title, note, filename, file_path," +
                    " created_at, submitted_at, received_at) VALUES (?,?,?,?,?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE title=VALUES(title), note=VALUES(note), filename=VALUES(filename)," +
                    " file_path=VALUES(file_path), submitted_at=VALUES(submitted_at), received_at=VALUES(received_at)",
                    docId, vo.productId, doc.docType, doc.title, doc.note, doc.filename, doc.filePath,
                    doc.createdAt   != null ? new Timestamp(doc.createdAt.getTime())   : null,
                    doc.submittedAt != null ? new Timestamp(doc.submittedAt.getTime()) : null,
                    doc.receivedAt  != null ? new Timestamp(doc.receivedAt.getTime())  : null
                );
            }
        }
    }
}
