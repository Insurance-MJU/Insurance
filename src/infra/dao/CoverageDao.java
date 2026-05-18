package infra.dao;

import domain.*;
import java.util.*;

public class CoverageDao {
    private static final CoverageDao INSTANCE = new CoverageDao();
    public static CoverageDao getInstance() { return INSTANCE; }

    private static final List<Coverage> STORE = new ArrayList<>();
    static {
        Coverage cov1 = new Coverage();
        cov1.setCoverageId("COV-001"); cov1.setCoverageName("대인배상 I");
        cov1.setCoverageType(CoverageType.PERSONAL_INJURY_MANDATORY); cov1.setMandatory(true);
        cov1.setLimitOptions(Collections.singletonList(opt("OPT-001-1", 1, "기본 옵션")));
        STORE.add(cov1);

        Coverage cov2 = new Coverage();
        cov2.setCoverageId("COV-002"); cov2.setCoverageName("대인배상 II");
        cov2.setCoverageType(CoverageType.PERSONAL_INJURY_OPTIONAL); cov2.setMandatory(false);
        cov2.setLimitOptions(Arrays.asList(opt("OPT-002-1", 1, "한도5억"), opt("OPT-002-2", 2, "무한")));
        STORE.add(cov2);

        Coverage cov3 = new Coverage();
        cov3.setCoverageId("COV-003"); cov3.setCoverageName("대물배상");
        cov3.setCoverageType(CoverageType.PROPERTY_DAMAGE); cov3.setMandatory(false);
        cov3.setLimitOptions(Collections.singletonList(opt("OPT-003-1", 1, "기본옵션")));
        STORE.add(cov3);

        Coverage cov4 = new Coverage();
        cov4.setCoverageId("COV-004"); cov4.setCoverageName("자동차상해");
        cov4.setCoverageType(CoverageType.AUTO_INJURY); cov4.setMandatory(false);
        cov4.setLimitOptions(Collections.singletonList(opt("OPT-004-1", 1, "기본옵션")));
        STORE.add(cov4);

        Coverage cov5 = new Coverage();
        cov5.setCoverageId("COV-005"); cov5.setCoverageName("자기차량손해");
        cov5.setCoverageType(CoverageType.OWN_VEHICLE_DAMAGE); cov5.setMandatory(false);
        cov5.setLimitOptions(Collections.singletonList(opt("OPT-005-1", 1, "기본옵션")));
        STORE.add(cov5);

        Coverage cov6 = new Coverage();
        cov6.setCoverageId("COV-006"); cov6.setCoverageName("무보험차상해");
        cov6.setCoverageType(CoverageType.UNINSURED_VEHICLE); cov6.setMandatory(false);
        cov6.setLimitOptions(Collections.singletonList(opt("OPT-006-1", 1, "기본옵션")));
        STORE.add(cov6);
    }

    private static CoverageLimitOption opt(String optId, int seq, String name) {
        CoverageLimitOption o = new CoverageLimitOption();
        o.setOptionId(seq); o.setCoverageMasterId(optId); o.setOptionName(name);
        return o;
    }

    public List<Coverage> findAll() { return Collections.unmodifiableList(STORE); }

    public Coverage findById(String coverageId) {
        return STORE.stream().filter(c -> c.getCoverageId().equals(coverageId)).findFirst().orElse(null);
    }
}
