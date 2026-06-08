package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExclusionDao {

    private final Database db;

    public ExclusionDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Map<String, Object>> EX = rs -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id",                     rs.getInt("id"));
        m.put("exclusionType",          rs.getString("exclusion_type"));
        m.put("exclusionTypeDisplayName", typeLabel(rs.getString("exclusion_type")));
        m.put("name",                   rs.getString("name"));
        m.put("description",            rs.getString("description"));
        m.put("subItems",               new ArrayList<>());
        return m;
    };

    private static final ResultSetExtractor<Map<String, Object>> SUB_EX = rs -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id",          rs.getInt("id"));
        m.put("exclusionId", rs.getInt("exclusion_id"));
        m.put("content",     rs.getString("content"));
        return m;
    };

    private static String typeLabel(String type) {
        if (type == null) return "";
        return switch (type) {
            case "WILLFUL"     -> "고의";
            case "WAR"         -> "전쟁·재해";
            case "ILLEGAL"     -> "법령위반";
            case "RACING"      -> "경주·시험";
            case "MISUSE"      -> "용도위반";
            case "SCOPE"       -> "운전범위위반";
            case "MAINTENANCE" -> "차량불량";
            case "OTHER"       -> "기타";
            default            -> type;
        };
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findAll() {
        List<Map<String, Object>> list = db.queryForList(
            "SELECT * FROM exclusions ORDER BY exclusion_type, id", EX);
        for (Map<String, Object> e : list) {
            int eid = (int) e.get("id");
            List<Map<String, Object>> subs = db.queryForList(
                "SELECT * FROM exclusion_sub_items WHERE exclusion_id = ? ORDER BY id",
                SUB_EX, eid);
            ((List<Object>) e.get("subItems")).addAll(subs);
        }
        return list;
    }

    public int save(Map<?, ?> data) {
        db.execute(
            "INSERT INTO exclusions (exclusion_type, name, description) VALUES (?, ?, ?)",
            str(data, "exclusionType"), str(data, "name"), str(data, "description"));
        Integer id = db.queryForObject("SELECT LAST_INSERT_ID()", rs -> rs.getInt(1));
        return id != null ? id : 0;
    }

    public void delete(String id) {
        db.execute("DELETE FROM exclusion_sub_items WHERE exclusion_id = ?", Integer.parseInt(id));
        db.execute("DELETE FROM exclusions WHERE id = ?", Integer.parseInt(id));
    }

    private static String str(Map<?, ?> m, String k) {
        Object v = m.get(k); return v != null ? v.toString() : null;
    }
}
