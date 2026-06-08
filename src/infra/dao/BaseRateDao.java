package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseRateDao {

    private final Database db;

    public BaseRateDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Map<String, Object>> RATE_EX = rs -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id",            rs.getInt("id"));
        m.put("rateType",      rs.getString("rate_type"));
        m.put("dimension1",    rs.getString("dimension1"));
        m.put("dimension2",    rs.getString("dimension2"));
        m.put("rateValue",     rs.getDouble("rate_value"));
        m.put("effectiveYear", rs.getInt("effective_year"));
        m.put("note",          rs.getString("note"));
        return m;
    };

    private static final ResultSetExtractor<Map<String, Object>> STAT_EX = rs -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id",               rs.getInt("id"));
        m.put("statType",         rs.getString("stat_type"));
        m.put("statYear",         rs.getInt("stat_year"));
        m.put("dimension1",       rs.getString("dimension1"));
        m.put("dimension2",       rs.getString("dimension2"));
        m.put("dimension3",       rs.getString("dimension3"));
        m.put("lossAmount",       rs.getLong("loss_amount"));
        m.put("deathCount",       rs.getInt("death_count"));
        m.put("injuryCount",      rs.getInt("injury_count"));
        m.put("totalLossCount",   rs.getInt("total_loss_count"));
        m.put("partialLossCount", rs.getInt("partial_loss_count"));
        return m;
    };

    public List<Map<String, Object>> findAll(String type) {
        if (type != null && !type.isBlank()) {
            return db.queryForList(
                "SELECT * FROM base_rates WHERE rate_type = ? ORDER BY id",
                RATE_EX, type);
        }
        return db.queryForList("SELECT * FROM base_rates ORDER BY rate_type, id", RATE_EX);
    }

    public List<Map<String, Object>> findStats(String type) {
        if (type != null && !type.isBlank()) {
            return db.queryForList(
                "SELECT * FROM base_rate_stats WHERE stat_type = ? ORDER BY stat_year DESC, id",
                STAT_EX, type);
        }
        return db.queryForList(
            "SELECT * FROM base_rate_stats ORDER BY stat_type, stat_year DESC, id", STAT_EX);
    }

    public void save(Map<?, ?> data) {
        db.execute(
            "INSERT INTO base_rates (rate_type, dimension1, dimension2, rate_value, effective_year, note)" +
            " VALUES (?, ?, ?, ?, ?, ?)",
            str(data, "rateType"), str(data, "dimension1"), str(data, "dimension2"),
            dbl(data, "rateValue"), intVal(data, "effectiveYear"), str(data, "note"));
    }

    public void delete(String id) {
        db.execute("DELETE FROM base_rates WHERE id = ?", Integer.parseInt(id));
    }

    private static String str(Map<?, ?> m, String k) {
        Object v = m.get(k); return v != null ? v.toString() : null;
    }
    private static double dbl(Map<?, ?> m, String k) {
        Object v = m.get(k); return v != null ? Double.parseDouble(v.toString()) : 0;
    }
    private static int intVal(Map<?, ?> m, String k) {
        Object v = m.get(k); return v != null ? (int) Double.parseDouble(v.toString()) : 0;
    }
}
