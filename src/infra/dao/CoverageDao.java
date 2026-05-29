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

    public void saveNew(java.util.Map<String, Object> data) {
        String coverageId   = "COV-" + System.currentTimeMillis();
        String coverageName = data.get("coverageName") != null ? (String) data.get("coverageName") : (String) data.get("name");
        String coverageType = (String) data.get("coverageType");
        int mandatory       = Boolean.TRUE.equals(data.get("mandatory")) ? 1 : 0;
        db.execute(
            "INSERT INTO coverages (coverage_id, coverage_name, coverage_type, mandatory) VALUES (?,?,?,?)",
            coverageId, coverageName, coverageType, mandatory);
        saveOptions(coverageId, data);
    }

    public void updateById(String coverageId, java.util.Map<String, Object> data) {
        String coverageName = data.get("coverageName") != null ? (String) data.get("coverageName") : (String) data.get("name");
        String coverageType = (String) data.get("coverageType");
        int mandatory       = Boolean.TRUE.equals(data.get("mandatory")) ? 1 : 0;
        db.execute(
            "UPDATE coverages SET coverage_name=?, coverage_type=?, mandatory=? WHERE coverage_id=?",
            coverageName, coverageType, mandatory, coverageId);
        db.execute("DELETE FROM coverage_limit_options WHERE coverage_master_id = ?", coverageId);
        saveOptions(coverageId, data);
    }

    public void delete(String coverageId) {
        db.execute("DELETE FROM coverage_limit_options WHERE coverage_master_id = ?", coverageId);
        db.execute("DELETE FROM coverages WHERE coverage_id = ?", coverageId);
    }

    @SuppressWarnings("unchecked")
    private void saveOptions(String coverageId, java.util.Map<String, Object> data) {
        Object raw = data.get("limitOptions");
        if (!(raw instanceof java.util.List<?> opts)) return;
        for (int i = 0; i < opts.size(); i++) {
            if (!(opts.get(i) instanceof java.util.Map<?, ?> opt)) continue;
            String optId   = "OPT-" + System.currentTimeMillis() + "-" + i;
            String optName = (String) ((java.util.Map<String, Object>) opt).get("optionName");
            db.execute(
                "INSERT INTO coverage_limit_options (option_id, coverage_master_id, seq, option_name) VALUES (?,?,?,?)",
                optId, coverageId, i + 1, optName);
        }
    }
}
