package infra.dao;

import domain.DamageInvestigation;
import domain.InjuryGrade;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DamageInvestigationDao {
    private final Database db;

    public DamageInvestigationDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<DamageInvestigation> EXTRACTOR = rs -> mapRow(rs);

    private static DamageInvestigation mapRow(ResultSet rs) throws SQLException {
        DamageInvestigation inv = new DamageInvestigation();
        inv.setInvestigationId(rs.getString("investigation_id"));
        inv.setAccidentId(rs.getString("accident_id"));
        inv.setInvestigatorName(rs.getString("investigator_name"));
        Timestamp invTs = rs.getTimestamp("investigation_date");
        // investigation_date not a field with getter/setter directly, skip
        inv.setOpinion(rs.getString("opinion"));
        inv.setDamageCode(rs.getString("damage_code"));
        int injuryGradeInt = rs.getInt("injury_grade");
        if (injuryGradeInt > 0) inv.setInjuryGrade(InjuryGrade.fromGrade(injuryGradeInt));
        inv.setOurFault(rs.getInt("our_fault"));
        inv.setOtherFault(rs.getInt("other_fault"));
        inv.setLiability(rs.getString("liability"));
        inv.setExpectedRepairCost(new Money(rs.getLong("expected_repair_cost"), "KRW"));
        inv.setCompensationLimit(new Money(rs.getLong("compensation_limit"), "KRW"));
        inv.setFinalOpinion(rs.getString("final_opinion"));
        Timestamp savedTs = rs.getTimestamp("saved_at");
        if (savedTs != null) inv.setSavedAt(new java.util.Date(savedTs.getTime()));

        inv.setClaimId(rs.getString("claim_id"));
        return inv;
    }

    public void save(DamageInvestigation inv) {
        // Generate investigation_id from accident_id if missing
        String invId = inv.getInvestigationId();
        if (invId == null || invId.isEmpty()) {
            invId = "INV-" + inv.getAccidentId();
            inv.setInvestigationId(invId);
        }
        String claimId = inv.getClaimId();

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
            invId,
            inv.getAccidentId(),
            claimId,
            inv.getInvestigatorName(),
            inv.getSavedAt() != null ? new Timestamp(inv.getSavedAt().getTime()) : null,
            inv.getOpinion(),
            inv.getDamageCode(),
            inv.getInjuryGrade() != null ? inv.getInjuryGrade().getGrade() : 0,
            inv.getOurFault(),
            inv.getOtherFault(),
            inv.getLiability(),
            inv.getExpectedRepairCost() != null ? inv.getExpectedRepairCost().getAmount() : 0L,
            inv.getCompensationLimit() != null ? inv.getCompensationLimit().getAmount() : 0L,
            inv.getFinalOpinion(),
            inv.getSavedAt() != null ? new Timestamp(inv.getSavedAt().getTime()) : null
        );
    }

    public DamageInvestigation findByAccidentId(String accidentId) {
        return db.queryForObject(
            "SELECT * FROM damage_investigations WHERE accident_id = ? LIMIT 1",
            EXTRACTOR, accidentId);
    }
}
