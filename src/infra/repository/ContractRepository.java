package infra.repository;

import domain.contract.Contract;
import domain.product.Party;
import domain.common.Money;
import infra.util.FileStore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContractRepository {

    private static final List<Contract> STORE;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    static {
        List<Contract> loaded = FileStore.load("contracts.dat");
        if (loaded != null) {
            STORE = loaded;
        } else {
            STORE = new ArrayList<>();
            initDefaults();
        }
    }

    private static void initDefaults() {
        STORE.add(build("IN-2026-001", "CNT-20240315-001",
            "MZ세대 다이렉트 개인용자동차보험",
            Contract.Status.ACTIVE, "2026-04-01", "2026-04-01", "2027-04-01",
            2_509_200L, "대인배상I, 대인배상II, 대물배상", "마일리지 특약",
            "64마0866", "박수현"));

        STORE.add(build("IN-2025-002", "CNT-20240520-002",
            "MZ세대 다이렉트 개인용자동차보험",
            Contract.Status.ACTIVE, "2025-06-15", "2025-06-15", "2026-06-15",
            1_980_000L, "대인배상I, 대인배상II, 대물배상, 자기차량손해", "블랙박스 할인특약",
            "12가3456", "김직원"));

        STORE.add(build("IN-2023-003", "CNT-20231210-003",
            "MZ세대 다이렉트 개인용자동차보험",
            Contract.Status.EXPIRED, "2023-12-10", "2023-12-10", "2024-12-10",
            2_100_000L, "대인배상I, 대물배상, 자기차량손해", "없음",
            "56다9012", "이영희"));

        FileStore.save("contracts.dat", STORE);
    }

    private static Contract build(String policyNo, String contractId, String productName,
                                  Contract.Status status, String issueDate, String startDate, String endDate,
                                  long premiumAmount, String coverages, String riders,
                                  String carNumber, String holderName) {
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
        try {
            c.setIssueDate(SDF.parse(issueDate));
            c.setStartDate(SDF.parse(startDate));
            c.setEndDate(SDF.parse(endDate));
        } catch (Exception ignored) {}
        return c;
    }

    public static List<Contract> findAll() {
        return new ArrayList<>(STORE);
    }

    public static Contract findByPolicyNo(String policyNo) {
        return STORE.stream()
            .filter(c -> c.getPolicyNo().equals(policyNo))
            .findFirst().orElse(null);
    }

    public static List<Contract> findByCondition(String holderName, String periodChoice, String statusChoice) {
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
                if ("1".equals(statusChoice)) return c.getStatus() == Contract.Status.ACTIVE;
                if ("2".equals(statusChoice)) return c.getStatus() == Contract.Status.EXPIRED;
                if ("3".equals(statusChoice)) return c.getStatus() == Contract.Status.CANCELLED;
                return true;
            })
            .collect(Collectors.toList());
    }

    public static void save(Contract contract) {
        STORE.removeIf(c -> c.getContractId().equals(contract.getContractId()));
        STORE.add(contract);
        FileStore.save("contracts.dat", STORE);
    }

    public static String nextPolicyNo() {
        return String.format("IN-2026-%03d", STORE.size() + 1);
    }

    public static String nextContractId() {
        return String.format("CNT-%d-%03d",
            java.time.LocalDate.now().getYear(), STORE.size() + 1);
    }
}
