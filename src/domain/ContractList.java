package domain;

import common.exception.domain.NotFoundException;
import domain.common.Money;
import infra.dao.ContractDao;
import infra.vo.ContractVO;
import infra.vo.SelectedCoverageVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ContractList {
    private final ContractDao dao;
    private final List<Contract> contracts;

    public ContractList(ContractDao dao) {
        this.dao       = dao;
        this.contracts = Collections.emptyList();
    }

    public ContractList(List<Contract> contracts) {
        this.dao       = null;
        this.contracts = Collections.unmodifiableList(contracts);
    }

    private static Contract toDomain(ContractVO vo) {
        if (vo == null) return null;
        Contract c = new Contract();
        c.setContractId(vo.contractId);
        c.setPolicyNo(vo.policyNo);
        c.setProductName(vo.productName);
        c.setSubscriptionNo(vo.subscriptionNo);
        c.setPremium(new Money(vo.premium, "KRW"));
        c.setCarNumber(vo.carNumber);
        c.setCoveragesDescription(vo.coveragesDescription);
        c.setCoverageLimit(vo.coverageLimit);
        c.setRidersDescription(vo.ridersDescription);
        c.setIssueDate(vo.issueDate);
        c.setStartDate(vo.startDate);
        c.setEndDate(vo.endDate);
        if (vo.status != null) c.setStatus(ContractStatus.valueOf(vo.status));
        if (vo.holderName != null) {
            Party holder = new Party();
            holder.setName(vo.holderName);
            holder.setPartyId(vo.holderPartyId);
            c.setPolicyholder(holder);
        }
        if (vo.selectedCoverages != null) {
            c.setSelectedCoverages(vo.selectedCoverages.stream()
                .map(ContractList::toSelectedCoverage)
                .collect(Collectors.toList()));
        }
        return c;
    }

    private static SelectedCoverage toSelectedCoverage(SelectedCoverageVO vo) {
        SelectedCoverage sc = new SelectedCoverage();
        sc.setCoverageMasterId(vo.coverageMasterId);
        sc.setCoverageName(vo.coverageName);
        sc.setMandatory(vo.mandatory);
        if (vo.deductibleType != null) {
            sc.setDeductibleType(Deductible.DeductibleType.valueOf(vo.deductibleType));
        }
        sc.setDeductibleAmount(new Money(vo.deductibleAmount, "KRW"));
        return sc;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public ContractList findAll() {
        return new ContractList(dao.findAll().stream().map(ContractList::toDomain).collect(Collectors.toList()));
    }

    public ContractList findByUserId(String userId) {
        return new ContractList(dao.findByUserId(userId).stream().map(ContractList::toDomain).collect(Collectors.toList()));
    }

    public ContractList findByCondition(String holderName, String periodChoice, String statusChoice) {
        return new ContractList(dao.findByCondition(holderName, periodChoice, statusChoice).stream()
            .map(ContractList::toDomain).collect(Collectors.toList()));
    }

    public Contract findByPolicyNo(String policyNo) {
        if (!contracts.isEmpty()) {
            return contracts.stream()
                .filter(c -> policyNo.equals(c.getPolicyNo()))
                .findFirst()
                .orElse(null);
        }
        return toDomain(dao.findByPolicyNo(policyNo));
    }

    public Contract findByContractId(String contractId) {
        return toDomain(dao.findByContractId(contractId));
    }

    public Contract findBySubscriptionNo(String subscriptionNo) {
        return toDomain(dao.findBySubscriptionNo(subscriptionNo));
    }

    public Contract getByContractId(String contractId) {
        Contract c = findByContractId(contractId);
        if (c == null) throw new NotFoundException("계약을 찾을 수 없습니다: " + contractId);
        return c;
    }

    public void save(Contract contract) {
        List<SelectedCoverageVO> scVOs = null;
        if (contract.getSelectedCoverages() != null) {
            scVOs = contract.getSelectedCoverages().stream()
                .map(sc -> new SelectedCoverageVO(
                    sc.getCoverageMasterId(), sc.getCoverageName(), sc.isMandatory(),
                    sc.getDeductibleType() != null ? sc.getDeductibleType().name() : null,
                    sc.getDeductibleAmount() != null ? sc.getDeductibleAmount().getAmount() : 0L
                ))
                .collect(Collectors.toList());
        }
        dao.save(new ContractVO(
            contract.getContractId(), contract.getPolicyNo(), contract.getProductName(),
            contract.getSubscriptionNo(),
            contract.getPremium() != null ? contract.getPremium().getAmount() : 0L,
            contract.getCarNumber(), contract.getCoveragesDescription(), contract.getCoverageLimit(),
            contract.getRidersDescription(), contract.getIssueDate(), contract.getStartDate(), contract.getEndDate(),
            contract.getStatus() != null ? contract.getStatus().name() : null,
            contract.getPolicyholder() != null ? contract.getPolicyholder().getName()    : null,
            contract.getPolicyholder() != null ? contract.getPolicyholder().getPartyId() : null,
            scVOs
        ));
    }

    public String nextPolicyNo() {
        return dao.nextPolicyNo();
    }

    public String nextContractId() {
        return dao.nextContractId();
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Contract> getAll() { return contracts; }
    public boolean isEmpty() { return contracts.isEmpty(); }
    public int size() { return contracts.size(); }

    public ContractList activeOnly() {
        return new ContractList(
            contracts.stream()
                .filter(Contract::isActive)
                .collect(Collectors.toList())
        );
    }

    public ContractList filterByStatus(ContractStatus status) {
        return new ContractList(
            contracts.stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList())
        );
    }
}
