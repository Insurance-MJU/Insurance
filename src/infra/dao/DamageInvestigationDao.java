package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.DamageInvestigationVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DamageInvestigationDao {
    private final Database db;

    public DamageInvestigationDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<DamageInvestigationVO> EXTRACTOR = rs -> mapRow(rs);

    private static DamageInvestigationVO mapRow(ResultSet rs) throws SQLException {
        Timestamp savedTs = rs.getTimestamp("saved_at");
        return new DamageInvestigationVO(
            rs.getString("investigation_id"),
            rs.getString("accident_id"),
            rs.getString("claim_id"),
            rs.getString("investigator_name"),
            rs.getString("opinion"),
            rs.getString("damage_code"),
            rs.getInt("injury_grade"),
            rs.getInt("our_fault"),
            rs.getInt("other_fault"),
            rs.getString("liability"),
            rs.getLong("expected_repair_cost"),
            rs.getLong("compensation_limit"),
            rs.getString("final_opinion"),
            savedTs != null ? new java.util.Date(savedTs.getTime()) : null
        );
    }

    public void save(DamageInvestigationVO vo) {
        String invId = vo.investigationId;
        if (invId == null || invId.isEmpty()) {
            invId = "INV-" + vo.accidentId;
        }
        db.execute(
            "INSERT INTO damage_investigations" +
            " (investigation_id, accident_id, claim_id, investigator_name, investigation_date," +
            " opinion, damage_code, injury_grade, our_fault, other_fault, liability," +
            " expected_repair_cost, compensation_limit, final_opinion, saved_at)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " accident_id=VALUES(accident_id), claim_id=VALUES(claim_id)," +
            " investigator_name=VALUES(investigator_name), investigation_date=VALUES(investigation_date)," +
            " opinion=VALUES(opinion), damage_code=VALUES(damage_code), injury_grade=VALUES(injury_grade)," +
            " our_fault=VALUES(our_fault), other_fault=VALUES(other_fault), liability=VALUES(liability)," +
            " expected_repair_cost=VALUES(expected_repair_cost), compensation_limit=VALUES(compensation_limit)," +
            " final_opinion=VALUES(final_opinion), saved_at=VALUES(saved_at)",
            invId, vo.accidentId, vo.claimId, vo.investigatorName,
            vo.savedAt != null ? new Timestamp(vo.savedAt.getTime()) : null,
            vo.opinion, vo.damageCode, vo.injuryGrade,
            vo.ourFault, vo.otherFault, vo.liability,
            vo.expectedRepairCost, vo.compensationLimit,
            vo.finalOpinion,
            vo.savedAt != null ? new Timestamp(vo.savedAt.getTime()) : null
        );
    }

    public DamageInvestigationVO findByAccidentId(String accidentId) {
        return db.queryForObject(
            "SELECT * FROM damage_investigations WHERE accident_id = ? LIMIT 1",
            EXTRACTOR, accidentId);
    }
}
