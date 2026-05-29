package infra.dao;

import domain.Accident;
import domain.AccidentStatus;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import domain.AccidentList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AccidentDao {
    private final Database db;

    public AccidentDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Accident> EXTRACTOR = rs -> mapRow(rs);

    private static Accident mapRow(ResultSet rs) throws SQLException {
        Accident a = new Accident();
        a.setAccidentId(rs.getString("accident_id"));
        a.setUserId(rs.getString("user_id"));
        Timestamp ts = rs.getTimestamp("accident_date");
        if (ts != null) a.setAccidentDate(new java.util.Date(ts.getTime()));
        a.setReportedBy(rs.getString("reported_by"));
        a.setPhone(rs.getString("phone"));
        a.setDescription(rs.getString("description"));
        a.setAccidentLocation(rs.getString("accident_location"));
        a.setAccidentDetail(rs.getString("accident_detail"));
        a.setDocuments(rs.getString("documents"));
        a.setContractId(rs.getString("contract_id"));
        a.setCoverageDescription(rs.getString("coverage_description"));
        a.setCoverageLimit(new Money(rs.getLong("coverage_limit"), "KRW"));
        a.setPersonalInjuryLimit(new Money(rs.getLong("personal_injury_limit"), "KRW"));
        a.setVehicleInfo(rs.getString("vehicle_info"));
        a.setExpectedRepairCost(new Money(rs.getLong("expected_repair_cost"), "KRW"));
        a.setRegionCode(rs.getString("region_code"));
        String statusStr = rs.getString("status");
        if (statusStr != null) a.setStatus(AccidentStatus.valueOf(statusStr));
        return a;
    }

    public AccidentList findByDateAndStatus(String date, String status) {
        if (status == null || status.isEmpty()) {
            return new AccidentList(db.queryForList(
                "SELECT * FROM accidents WHERE DATE(accident_date) LIKE ?",
                EXTRACTOR, date + "%"));
        }
        // status parameter is the label - find by enum name matching label
        // Try to match by enum name first
        String enumName = resolveStatusEnumName(status);
        if (enumName != null) {
            return new AccidentList(db.queryForList(
                "SELECT * FROM accidents WHERE DATE_FORMAT(accident_date,'%Y-%m-%d') LIKE ? AND status = ?",
                EXTRACTOR, date + "%", enumName));
        }
        return new AccidentList(db.queryForList(
            "SELECT * FROM accidents WHERE DATE_FORMAT(accident_date,'%Y-%m-%d') LIKE ?",
            EXTRACTOR, date + "%"));
    }

    private String resolveStatusEnumName(String label) {
        for (AccidentStatus s : AccidentStatus.values()) {
            if (s.getLabel().equals(label) || s.name().equals(label)) return s.name();
        }
        return null;
    }

    public AccidentList findPendingAccidents() {
        return new AccidentList(db.queryForList(
            "SELECT * FROM accidents WHERE status = ?",
            EXTRACTOR, AccidentStatus.PENDING.name()));
    }

    public Accident findById(String accidentId) {
        return db.queryForObject(
            "SELECT * FROM accidents WHERE accident_id = ?",
            EXTRACTOR, accidentId);
    }

    public Accident findByCustomerName(String name) {
        return db.queryForObject(
            "SELECT * FROM accidents WHERE reported_by = ? LIMIT 1",
            EXTRACTOR, name);
    }

    public AccidentList findByReportedBy(String reportedBy) {
        return new AccidentList(db.queryForList(
            "SELECT * FROM accidents WHERE reported_by = ? ORDER BY accident_date DESC",
            EXTRACTOR, reportedBy));
    }

    public AccidentList findByUserId(String userId) {
        return new AccidentList(db.queryForList(
            "SELECT * FROM accidents WHERE user_id = ? ORDER BY accident_date DESC",
            EXTRACTOR, userId));
    }

    public void save(Accident a) {
        db.execute(
            "INSERT INTO accidents (accident_id, user_id, accident_date, reported_by, phone, description," +
            " accident_location, accident_detail, documents, contract_id, coverage_description," +
            " coverage_limit, personal_injury_limit, vehicle_info, expected_repair_cost, region_code, status)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " user_id=VALUES(user_id), accident_date=VALUES(accident_date), reported_by=VALUES(reported_by), phone=VALUES(phone)," +
            " description=VALUES(description), accident_location=VALUES(accident_location)," +
            " accident_detail=VALUES(accident_detail), documents=VALUES(documents)," +
            " contract_id=VALUES(contract_id), coverage_description=VALUES(coverage_description)," +
            " coverage_limit=VALUES(coverage_limit), personal_injury_limit=VALUES(personal_injury_limit)," +
            " vehicle_info=VALUES(vehicle_info), expected_repair_cost=VALUES(expected_repair_cost)," +
            " region_code=VALUES(region_code), status=VALUES(status)",
            a.getAccidentId(),
            a.getUserId(),
            a.getAccidentDate() != null ? new Timestamp(a.getAccidentDate().getTime()) : null,
            a.getReportedBy(),
            a.getPhone(),
            a.getDescription(),
            a.getAccidentLocation(),
            a.getAccidentDetail(),
            a.getDocuments(),
            a.getContractId(),
            a.getCoverageDescription(),
            a.getCoverageLimit() != null ? a.getCoverageLimit().getAmount() : 0L,
            a.getPersonalInjuryLimit() != null ? a.getPersonalInjuryLimit().getAmount() : 0L,
            a.getVehicleInfo(),
            a.getExpectedRepairCost() != null ? a.getExpectedRepairCost().getAmount() : 0L,
            a.getRegionCode(),
            a.getStatus() != null ? a.getStatus().name() : null
        );
    }

    public String nextId() {
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM accidents", rs -> rs.getInt(1));
        int next = (count != null ? count : 0) + 1;
        return String.format("ACC-2026-%03d", next);
    }
}
