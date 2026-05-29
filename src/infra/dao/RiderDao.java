package infra.dao;

import domain.Rider;
import domain.RiderList;
import domain.RiderType;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

public class RiderDao {
    private final Database db;

    public RiderDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Rider> EXTRACTOR = rs -> {
        Rider r = new Rider();
        r.setRiderId(rs.getString("rider_id"));
        r.setRiderCode(rs.getString("rider_code"));
        r.setRiderName(rs.getString("rider_name"));
        r.setDescription(rs.getString("description"));
        String typeStr = rs.getString("rider_type");
        if (typeStr != null) {
            try { r.setRiderType(RiderType.valueOf(typeStr)); } catch (Exception ignored) {}
        }
        r.setMandatory(rs.getInt("mandatory") == 1);
        r.setDiscountRate(rs.getDouble("discount_rate"));
        return r;
    };

    public Rider findByCode(String riderCode) {
        return db.queryForObject(
            "SELECT * FROM riders WHERE rider_code = ?",
            EXTRACTOR, riderCode);
    }

    public RiderList findAll() {
        return new RiderList(db.queryForList("SELECT * FROM riders", EXTRACTOR));
    }

    public void saveNew(java.util.Map<String, Object> data) {
        String riderId   = "R-" + System.currentTimeMillis();
        String riderCode = (String) data.get("riderCode");
        String riderName = data.get("riderName") != null ? (String) data.get("riderName") : (String) data.get("name");
        String desc      = (String) data.get("description");
        String type      = (String) data.get("riderType");
        int mandatory    = Boolean.TRUE.equals(data.get("mandatory")) ? 1 : 0;
        double rate      = data.get("discountRate") instanceof Number n ? n.doubleValue() : 0.0;
        db.execute(
            "INSERT INTO riders (rider_id, rider_code, rider_name, description, rider_type, mandatory, discount_rate) VALUES (?,?,?,?,?,?,?)",
            riderId, riderCode, riderName, desc, type, mandatory, rate);
    }

    public void updateByCode(String riderCode, java.util.Map<String, Object> data) {
        String riderName = data.get("riderName") != null ? (String) data.get("riderName") : (String) data.get("name");
        String desc      = (String) data.get("description");
        String type      = (String) data.get("riderType");
        int mandatory    = Boolean.TRUE.equals(data.get("mandatory")) ? 1 : 0;
        double rate      = data.get("discountRate") instanceof Number n ? n.doubleValue() : 0.0;
        db.execute(
            "UPDATE riders SET rider_name=?, description=?, rider_type=?, mandatory=?, discount_rate=? WHERE rider_code=?",
            riderName, desc, type, mandatory, rate, riderCode);
    }

    public void deleteByCode(String riderCode) {
        db.execute("DELETE FROM riders WHERE rider_code = ?", riderCode);
    }
}
