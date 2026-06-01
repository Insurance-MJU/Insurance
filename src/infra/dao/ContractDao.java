package infra.dao;

import domain.Contract;
import domain.ContractStatus;
import domain.Deductible;
import domain.SelectedCoverage;
import domain.common.Money;
import infra.persistence.Database;
import infra.persistence.ResultSetExtractor;
import infra.vo.ContractVO;
import infra.vo.SelectedCoverageVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractDao {
    private final Database db;

    public ContractDao(Database db) { this.db = db; }

    private static final ResultSetExtractor<ContractVO> EXTRACTOR = rs -> mapRow(rs);

    private static ContractVO mapRow(ResultSet rs) throws SQLException {
        Timestamp issueTs  = rs.getTimestamp("issue_date");
        Timestamp startTs  = rs.getTimestamp("start_date");
        Timestamp endTs    = rs.getTimestamp("end_date");
        return new ContractVO(
            rs.getString("contract_id"),
            rs.getString("policy_no"),
            rs.getString("product_name"),
            rs.getString("subscription_no"),
            rs.getLong("premium"),
            rs.getString("car_number"),
            rs.getString("coverages_description"),
            rs.getString("coverage_limit"),
            rs.getString("riders_description"),
            issueTs  != null ? new java.util.Date(issueTs.getTime())  : null,
            startTs  != null ? new java.util.Date(startTs.getTime())  : null,
            endTs    != null ? new java.util.Date(endTs.getTime())    : null,
            rs.getString("status"),
            rs.getString("holder_name"),
            rs.getString("holder_party_id"),
            null
        );
    }

    private static final ResultSetExtractor<SelectedCoverageVO> SC_EXTRACTOR = rs ->
        new SelectedCoverageVO(
            rs.getString("coverage_master_id"),
            rs.getString("coverage_name"),
            rs.getInt("mandatory") == 1,
            rs.getString("deductible_type"),
            rs.getLong("deductible_amount")
        );

    private List<SelectedCoverageVO> loadSelectedCoverages(String contractId) {
        return db.queryForList(
            "SELECT * FROM contract_selected_coverages WHERE contract_id = ?",
            SC_EXTRACTOR, contractId);
    }

    private ContractVO loadFull(ContractVO vo) {
        if (vo == null) return null;
        List<SelectedCoverageVO> scs = loadSelectedCoverages(vo.contractId);
        return new ContractVO(
            vo.contractId, vo.policyNo, vo.productName, vo.subscriptionNo,
            vo.premium, vo.carNumber, vo.coveragesDescription, vo.coverageLimit,
            vo.ridersDescription, vo.issueDate, vo.startDate, vo.endDate,
            vo.status, vo.holderName, vo.holderPartyId, scs
        );
    }

    public List<ContractVO> findAll() {
        List<ContractVO> list = db.queryForList("SELECT * FROM contracts", EXTRACTOR);
        List<ContractVO> result = new ArrayList<>();
        for (ContractVO vo : list) result.add(loadFull(vo));
        return result;
    }

    public List<ContractVO> findByUserId(String userId) {
        List<ContractVO> list = db.queryForList(
            "SELECT c.* FROM contracts c JOIN subscriptions s ON c.subscription_no = s.subscription_no WHERE s.user_id = ?",
            EXTRACTOR, userId);
        List<ContractVO> result = new ArrayList<>();
        for (ContractVO vo : list) result.add(loadFull(vo));
        return result;
    }

    public ContractVO findByPolicyNo(String policyNo) {
        ContractVO vo = db.queryForObject(
            "SELECT * FROM contracts WHERE policy_no = ?", EXTRACTOR, policyNo);
        return loadFull(vo);
    }

    public ContractVO findByContractId(String contractId) {
        ContractVO vo = db.queryForObject(
            "SELECT * FROM contracts WHERE contract_id = ?", EXTRACTOR, contractId);
        return loadFull(vo);
    }

    public ContractVO findBySubscriptionNo(String subscriptionNo) {
        return db.queryForObject(
            "SELECT * FROM contracts WHERE subscription_no = ?", EXTRACTOR, subscriptionNo);
    }

    public List<ContractVO> findByCondition(String holderName, String periodChoice, String statusChoice) {
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

        List<ContractVO> list = db.queryForList(sql.toString(), EXTRACTOR, params.toArray());
        List<ContractVO> result = new ArrayList<>();
        for (ContractVO vo : list) result.add(loadFull(vo));
        return result;
    }

    public void save(Contract c) {
        String holderName    = (c.getPolicyholder() != null) ? c.getPolicyholder().getName()    : null;
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
            c.getIssueDate()  != null ? new Timestamp(c.getIssueDate().getTime())  : null,
            c.getStartDate()  != null ? new Timestamp(c.getStartDate().getTime())  : null,
            c.getEndDate()    != null ? new Timestamp(c.getEndDate().getTime())    : null,
            c.getStatus()     != null ? c.getStatus().name() : null,
            holderName,
            holderPartyId
        );

        if (c.getSelectedCoverages() != null) {
            db.execute("DELETE FROM contract_selected_coverages WHERE contract_id = ?", c.getContractId());
            for (SelectedCoverage sc : c.getSelectedCoverages()) {
                String id = c.getContractId() + "-" + sc.getCoverageMasterId();
                String dedType = sc.getDeductibleType() != null ? sc.getDeductibleType().name() : "NONE";
                long dedAmt = sc.getDeductibleAmount() != null ? sc.getDeductibleAmount().getAmount() : 0L;
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
