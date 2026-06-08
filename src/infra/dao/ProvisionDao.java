package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvisionDao {

    private final Database db;

    public ProvisionDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Map<String, Object>> PROV_EX = rs -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id",            rs.getInt("id"));
        m.put("provisionType", rs.getString("provision_type"));
        m.put("title",         rs.getString("title"));
        m.put("description",   rs.getString("description"));
        return m;
    };

    private static final ResultSetExtractor<Map<String, Object>> ITEM_EX = rs -> {
        Map<String, Object> m = new HashMap<>();
        m.put("id",           rs.getInt("id"));
        m.put("provisionId",  rs.getInt("provision_id"));
        m.put("articleNo",    rs.getInt("article_no"));
        m.put("articleTitle", rs.getString("article_title"));
        m.put("content",      rs.getString("content"));
        m.put("parentId",     rs.getObject("parent_id"));
        return m;
    };

    public List<Map<String, Object>> findAll() {
        return db.queryForList(
            "SELECT * FROM standard_provisions ORDER BY provision_type, id", PROV_EX);
    }

    public Map<String, Object> findById(String id) {
        return db.queryForObject(
            "SELECT * FROM standard_provisions WHERE id = ?", PROV_EX, Integer.parseInt(id));
    }

    public List<Map<String, Object>> findItems(String provisionId) {
        return db.queryForList(
            "SELECT * FROM provision_items WHERE provision_id = ? ORDER BY article_no",
            ITEM_EX, Integer.parseInt(provisionId));
    }

    public int save(Map<?, ?> data) {
        db.execute(
            "INSERT INTO standard_provisions (provision_type, title, description) VALUES (?, ?, ?)",
            str(data, "provisionType"), str(data, "title"), str(data, "description"));
        Integer id = db.queryForObject("SELECT LAST_INSERT_ID()", rs -> rs.getInt(1));
        return id != null ? id : 0;
    }

    public void delete(String id) {
        db.execute("DELETE FROM provision_items WHERE provision_id = ?", Integer.parseInt(id));
        db.execute("DELETE FROM standard_provisions WHERE id = ?", Integer.parseInt(id));
    }

    public int saveItem(String provisionId, Map<?, ?> data) {
        db.execute(
            "INSERT INTO provision_items (provision_id, article_no, article_title, content, parent_id)" +
            " VALUES (?, ?, ?, ?, ?)",
            Integer.parseInt(provisionId),
            intVal(data, "articleNo"),
            str(data, "articleTitle"),
            str(data, "content"),
            data.get("parentId") != null ? intVal(data, "parentId") : null);
        Integer id = db.queryForObject("SELECT LAST_INSERT_ID()", rs -> rs.getInt(1));
        return id != null ? id : 0;
    }

    public void deleteItem(String id) {
        db.execute("DELETE FROM provision_items WHERE id = ?", Integer.parseInt(id));
    }

    private static String str(Map<?, ?> m, String k) {
        Object v = m.get(k); return v != null ? v.toString() : null;
    }
    private static int intVal(Map<?, ?> m, String k) {
        Object v = m.get(k); return v != null ? (int) Double.parseDouble(v.toString()) : 0;
    }
}
