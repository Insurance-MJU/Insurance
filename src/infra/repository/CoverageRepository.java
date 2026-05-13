package infra.repository;

import domain.Coverage;
import domain.CoverageLimitOption;
import java.util.*;

public class CoverageRepository {
    private static final List<Coverage> STORE = new ArrayList<>();

    static {
        // ── 1. 대인배상 I (필수) ──────────────────────────────
        Coverage cov1 = new Coverage();
        cov1.setCoverageId("COV-001");
        cov1.setCoverageName("대인배상 I");
        cov1.setCoverageType(Coverage.CoverageType.PERSONAL_INJURY_MANDATORY);
        cov1.setMandatory(true);
        cov1.setLimitOptions(Collections.singletonList(option("OPT-001-1", "COV-001", 1, "기본 옵션")));
        STORE.add(cov1);

        // ── 2. 대인배상 II ────────────────────────────────────
        Coverage cov2 = new Coverage();
        cov2.setCoverageId("COV-002");
        cov2.setCoverageName("대인배상 II");
        cov2.setCoverageType(Coverage.CoverageType.PERSONAL_INJURY_OPTIONAL);
        cov2.setMandatory(false);
        cov2.setLimitOptions(Arrays.asList(
                option("OPT-002-1", "COV-002", 1, "한도5억"),
                option("OPT-002-2", "COV-002", 2, "무한")
        ));
        STORE.add(cov2);

        // ── 3. 대물배상 ───────────────────────────────────────
        Coverage cov3 = new Coverage();
        cov3.setCoverageId("COV-003");
        cov3.setCoverageName("대물배상");
        cov3.setCoverageType(Coverage.CoverageType.PROPERTY_DAMAGE);
        cov3.setMandatory(false);
        cov3.setLimitOptions(Collections.singletonList(option("OPT-003-1", "COV-003", 1, "기본옵션")));
        STORE.add(cov3);

        // ── 4. 자동차상해 ─────────────────────────────────────
        Coverage cov4 = new Coverage();
        cov4.setCoverageId("COV-004");
        cov4.setCoverageName("자동차상해");
        cov4.setCoverageType(Coverage.CoverageType.AUTO_INJURY);
        cov4.setMandatory(false);
        cov4.setLimitOptions(Collections.singletonList(option("OPT-004-1", "COV-004", 1, "기본옵션")));
        STORE.add(cov4);

        // ── 5. 자기차량손해 ───────────────────────────────────
        Coverage cov5 = new Coverage();
        cov5.setCoverageId("COV-005");
        cov5.setCoverageName("자기차량손해");
        cov5.setCoverageType(Coverage.CoverageType.OWN_VEHICLE_DAMAGE);
        cov5.setMandatory(false);
        cov5.setLimitOptions(Collections.singletonList(option("OPT-005-1", "COV-005", 1, "기본옵션")));
        STORE.add(cov5);

        // ── 6. 무보험차상해 ───────────────────────────────────
        Coverage cov6 = new Coverage();
        cov6.setCoverageId("COV-006");
        cov6.setCoverageName("무보험차상해");
        cov6.setCoverageType(Coverage.CoverageType.UNINSURED_VEHICLE);
        cov6.setMandatory(false);
        cov6.setLimitOptions(Collections.singletonList(option("OPT-006-1", "COV-006", 1, "기본옵션")));
        STORE.add(cov6);
    }

    public List<Coverage> findAll() {
        return Collections.unmodifiableList(STORE);
    }

    public Coverage findById(String coverageId) {
        return STORE.stream()
                .filter(c -> c.getCoverageId().equals(coverageId))
                .findFirst().orElse(null);
    }

    private static CoverageLimitOption option(String optId, String covMasterId, int seq, String name) {
        CoverageLimitOption opt = new CoverageLimitOption();
        opt.setOptionId(seq);
        opt.setCoverageMasterId(covMasterId);
        opt.setOptionName(name);
        return opt;
    }
}
