package infra.dao;

import domain.Coverage;
import domain.CoverageLimitOption;
import domain.CoverageType;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import java.util.List;

public class CoverageDao {
    private final Database db;

    public CoverageDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<CoverageLimitOption> OPT_EXTRACTOR = rs -> {
        CoverageLimitOption opt = new CoverageLimitOption();
        opt.setCoverageMasterId(rs.getString("coverage_master_id"));
        opt.setOptionId(rs.getInt("seq"));
        opt.setOptionName(rs.getString("option_name"));
        return opt;
    };

    private static final ResultSetExtractor<Coverage> EXTRACTOR = rs -> {
        Coverage cov = new Coverage();
        cov.setCoverageId(rs.getString("coverage_id"));
        cov.setCoverageName(rs.getString("coverage_name"));
        cov.setMandatory(rs.getInt("mandatory") == 1);
        String typeStr = rs.getString("coverage_type");
        if (typeStr != null) cov.setCoverageType(CoverageType.valueOf(typeStr));
        return cov;
    };

    private List<CoverageLimitOption> loadOptions(String coverageId) {
        return db.queryForList(
            "SELECT * FROM coverage_limit_options WHERE coverage_master_id = ? ORDER BY seq",
            OPT_EXTRACTOR, coverageId);
    }

    private Coverage loadFull(Coverage cov) {
        if (cov != null) {
            cov.setLimitOptions(loadOptions(cov.getCoverageId()));
        }
        return cov;
    }

    public List<Coverage> findAll() {
        List<Coverage> list = db.queryForList("SELECT * FROM coverages", EXTRACTOR);
        list.forEach(this::loadFull);
        return list;
    }

    public Coverage findById(String coverageId) {
        Coverage cov = db.queryForObject(
            "SELECT * FROM coverages WHERE coverage_id = ?", EXTRACTOR, coverageId);
        return loadFull(cov);
    }
}
