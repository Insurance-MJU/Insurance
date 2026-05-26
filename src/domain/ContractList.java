package domain;

import infra.dao.ContractDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ContractList {
    private final ContractDao dao;
    private final List<Contract> contracts;

    public ContractList(ContractDao dao) {
        this.dao = dao;
        this.contracts = Collections.emptyList();
    }

    public ContractList(List<Contract> contracts) {
        this.dao = null;
        this.contracts = Collections.unmodifiableList(contracts);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public ContractList findAll() {
        return dao.findAll();
    }

    public ContractList findByCondition(String holderName, String periodChoice, String statusChoice) {
        return dao.findByCondition(holderName, periodChoice, statusChoice);
    }

    public Contract findByPolicyNo(String policyNo) {
        if (!contracts.isEmpty()) {
            return contracts.stream()
                .filter(c -> policyNo.equals(c.getPolicyNo()))
                .findFirst()
                .orElse(null);
        }
        return dao.findByPolicyNo(policyNo);
    }

    public Contract findByContractId(String contractId) {
        return dao.findByContractId(contractId);
    }

    public void save(Contract contract) {
        dao.save(contract);
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
