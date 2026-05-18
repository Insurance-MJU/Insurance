package infra.dao;

import domain.*;
import domain.common.Money;
import infra.util.FileStore;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import static domain.Deductible.fixedAmount;
import static domain.Deductible.none;

public class ContractDao {
    private static final ContractDao INSTANCE = new ContractDao();
    public static ContractDao getInstance() { return INSTANCE; }

    private static final List<Contract> STORE;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    static {
        List<Contract> loaded = FileStore.load("contracts.dat");
        if (loaded != null) { STORE = loaded; }
        else { STORE = new ArrayList<>(); initDefaults(); }
    }

    private static void initDefaults() {
        Contract c1 = buildContract("IN-2026-001", "CNT-20240315-001",
            "MZ세대 다이렉트 개인용자동차보험",
            ContractStatus.ACTIVE, "2026-04-01", "2026-04-01", "2027-04-01",
            2_509_200L, "대인배상I, 대인배상II, 대물배상", "마일리지 특약",
            "64마0866", "박수현");
        c1.setSelectedCoverages(Arrays.asList(
            cov("COV-001", "대인배상 I",   true,  none()),
            cov("COV-002", "대인배상 II",  false, none()),
            cov("COV-003", "대물배상",     false, none())
        ));
        STORE.add(c1);

        Contract c2 = buildContract("IN-2025-002", "CNT-20240520-002",
            "MZ세대 다이렉트 개인용자동차보험",
            ContractStatus.ACTIVE, "2025-06-15", "2025-06-15", "2026-06-15",
            1_980_000L, "대인배상I, 대인배상II, 대물배상, 자기차량손해", "블랙박스 할인특약",
            "12가3456", "김직원");
        c2.setSelectedCoverages(Arrays.asList(
            cov("COV-001", "대인배상 I",    true,  none()),
            cov("COV-002", "대인배상 II",   false, none()),
            cov("COV-003", "대물배상",      false, none()),
            cov("COV-005", "자기차량손해",  false, fixedAmount(new Money(200_000L, "KRW")))
        ));
        STORE.add(c2);

        Contract c3 = buildContract("IN-2023-003", "CNT-20231210-003",
            "MZ세대 다이렉트 개인용자동차보험",
            ContractStatus.EXPIRED, "2023-12-10", "2023-12-10", "2024-12-10",
            2_100_000L, "대인배상I, 대물배상, 자기차량손해", "없음",
            "56다9012", "이영희");
        c3.setSelectedCoverages(Arrays.asList(
            cov("COV-001", "대인배상 I",    true,  none()),
            cov("COV-003", "대물배상",      false, none()),
            cov("COV-005", "자기차량손해",  false, fixedAmount(new Money(200_000L, "KRW")))
        ));
        STORE.add(c3);

        FileStore.save("contracts.dat", STORE);
    }

    private static SelectedCoverage cov(String masterId, String name, boolean mandatory, Deductible ded) {
        SelectedCoverage sc = new SelectedCoverage();
        sc.setCoverageMasterId(masterId);
        sc.setCoverageName(name);
        sc.setMandatory(mandatory);
        sc.setDeductible(ded);
        return sc;
    }

    private static Contract buildContract(String policyNo, String contractId, String productName,
                                          ContractStatus status, String issueDate, String startDate,
                                          String endDate, long premiumAmount, String coverages,
                                          String riders, String carNumber, String holderName) {
        Party holder = new Party();
        holder.setPartyId("PARTY-" + contractId);
        holder.setName(holderName);
        Contract c = new Contract();
        c.setPolicyNo(policyNo);
        c.setContractId(contractId);
        c.setProductName(productName);
        c.setStatus(status);
        c.setPolicyholder(holder);
        c.setPremium(new Money(premiumAmount, "KRW"));
        c.setCarNumber(carNumber);
        c.setCoveragesDescription(coverages);
        c.setRidersDescription(riders);
        try { c.setIssueDate(SDF.parse(issueDate)); c.setStartDate(SDF.parse(startDate)); c.setEndDate(SDF.parse(endDate)); } catch (Exception ignored) {}
        return c;
    }

    public List<Contract> findAll() { return new ArrayList<>(STORE); }

    public Contract findByPolicyNo(String policyNo) {
        return STORE.stream().filter(c -> c.getPolicyNo().equals(policyNo)).findFirst().orElse(null);
    }

    public Contract findByContractId(String contractId) {
        return STORE.stream().filter(c -> c.getContractId().equals(contractId)).findFirst().orElse(null);
    }

    public List<Contract> findByCondition(String holderName, String periodChoice, String statusChoice) {
        String cutoff1 = LocalDate.now().minusYears(1).toString();
        String cutoff3 = LocalDate.now().minusYears(3).toString();
        return STORE.stream()
            .filter(c -> holderName.isEmpty()
                || (c.getPolicyholder() != null && holderName.equals(c.getPolicyholder().getName())))
            .filter(c -> {
                if ("2".equals(periodChoice)) return c.getIssueDateString().compareTo(cutoff1) >= 0;
                if ("3".equals(periodChoice)) return c.getIssueDateString().compareTo(cutoff3) >= 0;
                return true;
            })
            .filter(c -> {
                if ("1".equals(statusChoice)) return c.getStatus() == ContractStatus.ACTIVE;
                if ("2".equals(statusChoice)) return c.getStatus() == ContractStatus.EXPIRED;
                if ("3".equals(statusChoice)) return c.getStatus() == ContractStatus.CANCELLED;
                return true;
            })
            .collect(Collectors.toList());
    }

    public void save(Contract c) {
        STORE.removeIf(x -> x.getContractId().equals(c.getContractId()));
        STORE.add(c);
        FileStore.save("contracts.dat", STORE);
    }

    public String nextPolicyNo() {
        return String.format("IN-2026-%03d", STORE.size() + 1);
    }

    public String nextContractId() {
        return String.format("CNT-%d-%03d", LocalDate.now().getYear(), STORE.size() + 1);
    }
}
