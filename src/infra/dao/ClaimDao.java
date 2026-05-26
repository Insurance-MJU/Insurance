package infra.dao;

import domain.*;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import domain.ClaimList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ClaimDao {
    private final Database db;

    public ClaimDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Claim> EXTRACTOR = rs -> mapRow(rs);

    private static Claim mapRow(ResultSet rs) throws SQLException {
        Claim c = new Claim();
        c.setClaimId(rs.getString("claim_id"));
        c.setClaimantName(rs.getString("claimant_name"));
        Timestamp claimTs = rs.getTimestamp("claim_date");
        if (claimTs != null) c.setClaimDate(new java.util.Date(claimTs.getTime()));
        c.setContractId(rs.getString("contract_id"));
        c.setDescription(rs.getString("description"));
        String statusStr = rs.getString("claim_status");
        if (statusStr != null) c.setClaimStatus(ClaimStatus.valueOf(statusStr));
        c.setAssignedEmployee(rs.getString("assigned_employee"));

        // Reconstruct embedded Accident stub
        String accidentId = rs.getString("accident_id");
        if (accidentId != null) {
            Accident acc = new Accident();
            acc.setAccidentId(accidentId);
            c.setAccident(acc);
        }

        // Reconstruct DamageAssessment
        long settlementAmt = rs.getLong("settlement_amount");
        long dedAmt = rs.getLong("deductible_amount");
        long compAmt = rs.getLong("compensation_amount");
        if (settlementAmt > 0 || compAmt > 0) {
            if (c.getDamageInvestigation() == null) c.setDamageInvestigation(new DamageInvestigation());
            c.getDamageInvestigation().setAssessment(new DamageAssessment(
                new Money(settlementAmt, "KRW"),
                new Money(dedAmt, "KRW"),
                new Money(compAmt, "KRW")));
        }

        // ClaimPayment
        String bankName = rs.getString("bank_name");
        String accountNo = rs.getString("account_number");
        if (bankName != null && !bankName.isEmpty()) {
            DamageAssessment da = c.getDamageAssessment();
            if (da == null) {
                if (c.getDamageInvestigation() == null) c.setDamageInvestigation(new DamageInvestigation());
                da = new DamageAssessment();
                c.getDamageInvestigation().setAssessment(da);
            }
            da.setClaimPayment(new ClaimPayment(bankName, accountNo));
        }

        return c;
    }

    public Claim findByAccidentId(String accidentId) {
        return db.queryForObject(
            "SELECT * FROM claims WHERE accident_id = ? LIMIT 1",
            EXTRACTOR, accidentId);
    }

    public Claim findById(String claimId) {
        return db.queryForObject(
            "SELECT * FROM claims WHERE claim_id = ?",
            EXTRACTOR, claimId);
    }

    public ClaimList findAwaitingPayment() {
        return new ClaimList(db.queryForList(
            "SELECT * FROM claims WHERE claim_status = ?",
            EXTRACTOR, ClaimStatus.PAYMENT_PENDING.name()));
    }

    public void save(Claim c) {
        String accidentId = (c.getAccident() != null) ? c.getAccident().getAccidentId() : null;
        long settlementAmt = 0L;
        long dedAmt = 0L;
        long compAmt = 0L;
        if (c.getDamageAssessment() != null) {
            DamageAssessment da = c.getDamageAssessment();
            settlementAmt = da.getSettlement() != null ? da.getSettlement().getAmount() : 0L;
            dedAmt = da.getDeductibleAmount() != null ? da.getDeductibleAmount().getAmount() : 0L;
            compAmt = da.getCompensationAmount() != null ? da.getCompensationAmount().getAmount() : 0L;
        }
        String bankName = c.getBankName();
        String accountNo = c.getAccountNumber();

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
            c.getClaimId(),
            accidentId,
            c.getClaimantName(),
            c.getClaimDate() != null ? new Timestamp(c.getClaimDate().getTime()) : null,
            c.getContractId(),
            c.getDescription(),
            c.getClaimStatus() != null ? c.getClaimStatus().name() : null,
            c.getAssignedEmployee(),
            settlementAmt,
            dedAmt,
            compAmt,
            bankName,
            accountNo
        );
    }

    public String nextId() {
        Integer count = db.queryForObject(
            "SELECT COUNT(*) FROM claims", rs -> rs.getInt(1));
        int next = (count != null ? count : 0) + 1;
        return String.format("CL-%05d", next);
    }
}
