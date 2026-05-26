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
}
