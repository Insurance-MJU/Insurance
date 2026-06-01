package infra.dao;

import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.ClaimVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ClaimDao {
    private final Database db;

    public ClaimDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<ClaimVO> EXTRACTOR = rs -> mapRow(rs);

    private static ClaimVO mapRow(ResultSet rs) throws SQLException {
        Timestamp claimTs = rs.getTimestamp("claim_date");
        return new ClaimVO(
            rs.getString("claim_id"),
            rs.getString("claimant_name"),
            claimTs != null ? new java.util.Date(claimTs.getTime()) : null,
            rs.getString("contract_id"),
            rs.getString("description"),
            rs.getString("claim_status"),
            rs.getString("assigned_employee"),
            rs.getString("accident_id"),
            rs.getLong("settlement_amount"),
            rs.getLong("deductible_amount"),
            rs.getLong("compensation_amount"),
            rs.getString("bank_name"),
            rs.getString("account_number")
        );
    }

    public ClaimVO findByAccidentId(String accidentId) {
        return db.queryForObject(
            "SELECT * FROM claims WHERE accident_id = ? LIMIT 1",
            EXTRACTOR, accidentId);
    }

    public ClaimVO findById(String claimId) {
        return db.queryForObject(
            "SELECT * FROM claims WHERE claim_id = ?",
            EXTRACTOR, claimId);
    }

    public List<ClaimVO> findAll() {
        return db.queryForList(
            "SELECT * FROM claims ORDER BY claim_date DESC",
            EXTRACTOR);
    }

    public List<ClaimVO> findAwaitingPayment() {
        return db.queryForList(
            "SELECT * FROM claims WHERE claim_status = 'PAYMENT_PENDING'",
            EXTRACTOR);
    }

    public void save(ClaimVO vo) {
        db.execute(
            "INSERT INTO claims (claim_id, accident_id, claimant_name, claim_date, contract_id," +
            " description, claim_status, assigned_employee," +
            " settlement_amount, deductible_amount, compensation_amount," +
            " bank_name, account_number)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " accident_id=VALUES(accident_id), claimant_name=VALUES(claimant_name)," +
            " claim_date=VALUES(claim_date), contract_id=VALUES(contract_id)," +
            " description=VALUES(description), claim_status=VALUES(claim_status)," +
            " assigned_employee=VALUES(assigned_employee), settlement_amount=VALUES(settlement_amount)," +
            " deductible_amount=VALUES(deductible_amount)," +
            " compensation_amount=VALUES(compensation_amount)," +
            " bank_name=VALUES(bank_name), account_number=VALUES(account_number)",
            vo.claimId,
            vo.accidentId,
            vo.claimantName,
            vo.claimDate != null ? new Timestamp(vo.claimDate.getTime()) : null,
            vo.contractId,
            vo.description,
            vo.claimStatus,
            vo.assignedEmployee,
            vo.settlementAmount,
            vo.deductibleAmount,
            vo.compensationAmount,
            vo.bankName,
            vo.accountNumber
        );
    }

    public String nextId() {
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM claims", rs -> rs.getInt(1));
        int next = (count != null ? count : 0) + 1;
        return String.format("CL-%05d", next);
    }
}
