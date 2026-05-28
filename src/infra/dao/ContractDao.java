package infra.dao;

import domain.*;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;

import domain.ContractList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractDao {
    private final Database db;

    public ContractDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<Contract> EXTRACTOR = rs -> mapRow(rs);

    private static Contract mapRow(ResultSet rs) throws SQLException {
        Contract c = new Contract();
        c.setContractId(rs.getString("contract_id"));
        c.setPolicyNo(rs.getString("policy_no"));
        c.setProductName(rs.getString("product_name"));
        c.setSubscriptionNo(rs.getString("subscription_no"));
        c.setPremium(new Money(rs.getLong("premium"), "KRW"));
        c.setCarNumber(rs.getString("car_number"));
        c.setCoveragesDescription(rs.getString("coverages_description"));
        c.setCoverageLimit(rs.getString("coverage_limit"));
        c.setRidersDescription(rs.getString("riders_description"));
        Timestamp issueTs = rs.getTimestamp("issue_date");
        if (issueTs != null) c.setIssueDate(new java.util.Date(issueTs.getTime()));
        Timestamp startTs = rs.getTimestamp("start_date");
        if (startTs != null) c.setStartDate(new java.util.Date(startTs.getTime()));
        Timestamp endTs = rs.getTimestamp("end_date");
        if (endTs != null) c.setEndDate(new java.util.Date(endTs.getTime()));
        String statusStr = rs.getString("status");
        if (statusStr != null) c.setStatus(ContractStatus.valueOf(statusStr));
        String holderName = rs.getString("holder_name");
        String holderPartyId = rs.getString("holder_party_id");
        if (holderName != null) {
            Party holder = new Party();
            holder.setName(holderName);
            holder.setPartyId(holderPartyId);
            c.setPolicyholder(holder);
        }
        return c;
    }

    private static final ResultSetExtractor<SelectedCoverage> SC_EXTRACTOR = rs -> {
        SelectedCoverage sc = new SelectedCoverage();
        sc.setCoverageMasterId(rs.getString("coverage_master_id"));
        sc.setCoverageName(rs.getString("coverage_name"));
        sc.setMandatory(rs.getInt("mandatory") == 1);
        String dedTypeStr = rs.getString("deductible_type");
        long dedAmt = rs.getLong("deductible_amount");
        if (dedTypeStr != null) {
            sc.setDeductibleType(Deductible.DeductibleType.valueOf(dedTypeStr));
        }
        sc.setDeductibleAmount(new Money(dedAmt, "KRW"));
        return sc;
    };

    private List<SelectedCoverage> loadSelectedCoverages(String contractId) {
        return db.queryForList(
            "SELECT * FROM contract_selected_coverages WHERE contract_id = ?",
            SC_EXTRACTOR, contractId);
    }

    private Contract loadFull(Contract c) {
        if (c != null) {
            c.setSelectedCoverages(loadSelectedCoverages(c.getContractId()));
        }
        return c;
    }

    public ContractList findAll() {
        List<Contract> list = db.queryForList("SELECT * FROM contracts", EXTRACTOR);
        list.forEach(this::loadFull);
        return new ContractList(list);
    }

    public Contract findByPolicyNo(String policyNo) {
        Contract c = db.queryForObject(
            "SELECT * FROM contracts WHERE policy_no = ?", EXTRACTOR, policyNo);
        return loadFull(c);
    }

    public Contract findByContractId(String contractId) {
        Contract c = db.queryForObject(
            "SELECT * FROM contracts WHERE contract_id = ?", EXTRACTOR, contractId);
        return loadFull(c);
    }

    public Contract findBySubscriptionNo(String subscriptionNo) {
        return db.queryForObject(
            "SELECT * FROM contracts WHERE subscription_no = ?", EXTRACTOR, subscriptionNo);
    }

    public ContractList findByCondition(String holderName, String periodChoice, String statusChoice) {
        StringBuilder sql = new StringBuilder("SELECT * FROM contracts WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (holderName != null && !holderName.isEmpty()) {
            sql.append(" AND holder_name = ?");
            params.add(holderName);
        }
        if ("2".equals(periodChoice)) {
            sql.append(" AND issue_date >= DATE_SUB(NOW(), INTERVAL 1 YEAR)");
        } else if ("3".equals(periodChoice)) {
            sql.append(" AND issue_date >= DATE_SUB(NOW(), INTERVAL 3 YEAR)");
        }
        if ("1".equals(statusChoice)) {
            sql.append(" AND status = 'ACTIVE'");
        } else if ("2".equals(statusChoice)) {
            sql.append(" AND status = 'EXPIRED'");
        } else if ("3".equals(statusChoice)) {
            sql.append(" AND status = 'CANCELLED'");
        }

        List<Contract> list = db.queryForList(sql.toString(), EXTRACTOR, params.toArray());
        list.forEach(this::loadFull);
        return new ContractList(list);
    }

    public void save(Contract c) {
        String holderName = (c.getPolicyholder() != null) ? c.getPolicyholder().getName() : null;
        String holderPartyId = (c.getPolicyholder() != null) ? c.getPolicyholder().getPartyId() : null;

        db.execute(
            "INSERT INTO contracts (contract_id, policy_no, product_name, subscription_no, premium," +
            " car_number, coverages_description, coverage_limit, riders_description," +
            " issue_date, start_date, end_date, status, holder_name, holder_party_id)" +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE" +
            " policy_no=VALUES(policy_no), product_name=VALUES(product_name)," +
            " subscription_no=VALUES(subscription_no), premium=VALUES(premium)," +
            " car_number=VALUES(car_number), coverages_description=VALUES(coverages_description)," +
            " coverage_limit=VALUES(coverage_limit), riders_description=VALUES(riders_description)," +
            " issue_date=VALUES(issue_date), start_date=VALUES(start_date), end_date=VALUES(end_date)," +
            " status=VALUES(status), holder_name=VALUES(holder_name), holder_party_id=VALUES(holder_party_id)",
            c.getContractId(),
            c.getPolicyNo(),
            c.getProductName(),
            c.getSubscriptionNo(),
            c.getPremium() != null ? c.getPremium().getAmount() : 0L,
            c.getCarNumber(),
            c.getCoveragesDescription(),
            c.getCoverageLimit(),
            c.getRidersDescription(),
            c.getIssueDate() != null ? new Timestamp(c.getIssueDate().getTime()) : null,
            c.getStartDate() != null ? new Timestamp(c.getStartDate().getTime()) : null,
            c.getEndDate() != null ? new Timestamp(c.getEndDate().getTime()) : null,
            c.getStatus() != null ? c.getStatus().name() : null,
            holderName,
            holderPartyId
        );

        // Save selected coverages: delete then reinsert
        if (c.getSelectedCoverages() != null) {
            db.execute("DELETE FROM contract_selected_coverages WHERE contract_id = ?", c.getContractId());
            for (SelectedCoverage sc : c.getSelectedCoverages()) {
                String id = c.getContractId() + "-" + sc.getCoverageMasterId();
                String dedType = sc.getDeductibleType() != null
                    ? sc.getDeductibleType().name() : "NONE";
                long dedAmt = sc.getDeductibleAmount() != null
                    ? sc.getDeductibleAmount().getAmount() : 0L;
                db.execute(
                    "INSERT INTO contract_selected_coverages" +
                    " (id, contract_id, coverage_master_id, coverage_name, mandatory, deductible_type, deductible_amount)" +
                    " VALUES (?,?,?,?,?,?,?)" +
                    " ON DUPLICATE KEY UPDATE coverage_name=VALUES(coverage_name)," +
                    " mandatory=VALUES(mandatory), deductible_type=VALUES(deductible_type)," +
                    " deductible_amount=VALUES(deductible_amount)",
                    id, c.getContractId(), sc.getCoverageMasterId(), sc.getCoverageName(),
                    sc.isMandatory() ? 1 : 0, dedType, dedAmt
                );
            }
        }
    }

    public String nextPolicyNo() {
        Integer count = db.queryForObject("SELECT COUNT(*) FROM contracts", rs -> rs.getInt(1));
        int next = (count != null ? count : 0) + 1;
        return String.format("IN-2026-%03d", next);
    }

    public String nextContractId() {
        Integer count = db.queryForObject("SELECT COUNT(*) FROM contracts", rs -> rs.getInt(1));
        int next = (count != null ? count : 0) + 1;
        return String.format("CNT-%d-%03d", LocalDate.now().getYear(), next);
    }
}
