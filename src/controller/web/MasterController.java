package controller.web;

import domain.Coverage;
import domain.Rider;
import infra.dao.BaseRateDao;
import infra.dao.CoverageDao;
import infra.dao.ExclusionDao;
import infra.dao.ProvisionDao;
import infra.dao.RiderDao;
import infra.web.Router;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MasterController {

    private final BaseRateDao   baseRateDao;
    private final ExclusionDao  exclusionDao;
    private final ProvisionDao  provisionDao;
    private final RiderDao      riderDao;
    private final CoverageDao   coverageDao;

    public MasterController(BaseRateDao baseRateDao, ExclusionDao exclusionDao, ProvisionDao provisionDao,
                            RiderDao riderDao, CoverageDao coverageDao) {
        this.baseRateDao  = baseRateDao;
        this.exclusionDao = exclusionDao;
        this.provisionDao = provisionDao;
        this.riderDao     = riderDao;
        this.coverageDao  = coverageDao;
    }

    public void registerRoutes(Router router) {
        // 기초율 요율 계수
        router.get("/master/base-rates",
                (req, res) -> res.ok(Map.of("data", baseRateDao.findAll(req.queryParam("type")))));
        router.post("/master/base-rates",
                (req, res) -> { baseRateDao.save(req.body(LinkedHashMap.class)); res.ok(Map.of("ok", true)); });
        router.delete("/master/base-rates/{id}",
                (req, res) -> { baseRateDao.delete(req.pathVariable("id")); res.noContent(); });

        // 기초율 통계 원본
        router.get("/master/base-rates/stats",
                (req, res) -> res.ok(Map.of("data", baseRateDao.findStats(req.queryParam("type")))));

        // 면책사유
        router.get("/master/exclusions",
                (req, res) -> res.ok(Map.of("data", exclusionDao.findAll())));
        router.post("/master/exclusions",
                (req, res) -> { int id = exclusionDao.save(req.body(LinkedHashMap.class)); res.created(Map.of("id", id)); });
        router.delete("/master/exclusions/{id}",
                (req, res) -> { exclusionDao.delete(req.pathVariable("id")); res.noContent(); });

        // 표준약관
        router.get("/master/provisions",
                (req, res) -> res.ok(Map.of("data", provisionDao.findAll())));
        router.get("/master/provisions/{id}",
                (req, res) -> res.ok(provisionDao.findById(req.pathVariable("id"))));
        router.post("/master/provisions",
                (req, res) -> { int id = provisionDao.save(req.body(LinkedHashMap.class)); res.created(Map.of("id", id)); });
        router.delete("/master/provisions/{id}",
                (req, res) -> { provisionDao.delete(req.pathVariable("id")); res.noContent(); });
        router.get("/master/provisions/{id}/items",
                (req, res) -> res.ok(Map.of("data", provisionDao.findItems(req.pathVariable("id")))));
        router.post("/master/provisions/{id}/items",
                (req, res) -> { int itemId = provisionDao.saveItem(req.pathVariable("id"), req.body(LinkedHashMap.class)); res.created(Map.of("id", itemId)); });
        router.delete("/master/provisions/items/{id}",
                (req, res) -> { provisionDao.deleteItem(req.pathVariable("id")); res.noContent(); });

        // 특약 (Rider) CRUD
        router.get("/master/riders/{id}",
                (req, res) -> res.ok(riderToMap(riderDao.findByCode(req.pathVariable("id")))));
        router.post("/master/riders",
                (req, res) -> { riderDao.saveNew(req.body(LinkedHashMap.class)); res.created(Map.of("ok", true)); });
        router.put("/master/riders/{id}",
                (req, res) -> { riderDao.updateByCode(req.pathVariable("id"), req.body(LinkedHashMap.class)); res.ok(Map.of("ok", true)); });
        router.delete("/master/riders/{id}",
                (req, res) -> { riderDao.deleteByCode(req.pathVariable("id")); res.noContent(); });

        // 담보 (Coverage) CRUD
        router.get("/master/coverages/{id}",
                (req, res) -> res.ok(coverageToMap(coverageDao.findById(req.pathVariable("id")))));
        router.post("/master/coverages",
                (req, res) -> { coverageDao.saveNew(req.body(LinkedHashMap.class)); res.created(Map.of("ok", true)); });
        router.put("/master/coverages/{id}",
                (req, res) -> { coverageDao.updateById(req.pathVariable("id"), req.body(LinkedHashMap.class)); res.ok(Map.of("ok", true)); });
        router.delete("/master/coverages/{id}",
                (req, res) -> { coverageDao.delete(req.pathVariable("id")); res.noContent(); });
    }

    private Map<String, Object> riderToMap(Rider r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",           r.getRiderCode());
        m.put("riderCode",    r.getRiderCode());
        m.put("name",         r.getRiderName());
        m.put("riderName",    r.getRiderName());
        m.put("description",  r.getDescription() != null ? r.getDescription() : "");
        m.put("riderType",    r.getRiderType() != null ? r.getRiderType().name() : "DISCOUNT");
        m.put("mandatory",    r.isMandatory());
        m.put("discountRate", r.getDiscountRate() != null ? r.getDiscountRate() : 0.0);
        m.put("exclusions",   List.of());
        m.put("provisionId",  null);
        return m;
    }

    private Map<String, Object> coverageToMap(Coverage c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",               c.getCoverageId());
        m.put("coverageId",       c.getCoverageId());
        m.put("name",             c.getCoverageName());
        m.put("coverageName",     c.getCoverageName());
        m.put("coverageType",     c.getCoverageType() != null ? c.getCoverageType().name() : null);
        m.put("mandatory",        c.isMandatory());
        m.put("description",      null);
        m.put("limitType",        null);
        m.put("limitAmount",      null);
        m.put("limitUnit",        null);
        m.put("compensationType", null);
        m.put("deductibleType",   null);
        m.put("deductibleAmount", null);
        m.put("deductibleRate",   null);
        m.put("autoRestoration",  false);
        m.put("excessPay",        false);
        m.put("provisionId",      null);
        m.put("exclusions",       List.of());
        m.put("requiredCoverages", List.of());
        m.put("limitOptions", c.getLimitOptions() != null
            ? c.getLimitOptions().stream().map(o -> Map.of(
                "id",         (Object) o.getOptionId(),
                "optionName", o.getOptionName() != null ? o.getOptionName() : ""
              )).collect(Collectors.toList())
            : List.of());
        return m;
    }
}
