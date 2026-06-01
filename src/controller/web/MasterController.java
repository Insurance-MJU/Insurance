package controller.web;

import infra.dao.BaseRateDao;
import infra.dao.CoverageDao;
import infra.dao.ExclusionDao;
import infra.dao.ProvisionDao;
import infra.dao.RiderDao;
import infra.vo.CoverageVO;
import infra.vo.CoverageLimitOptionVO;
import infra.vo.RiderVO;
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

    private Map<String, Object> riderToMap(RiderVO r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",           r.riderCode);
        m.put("riderCode",    r.riderCode);
        m.put("name",         r.riderName);
        m.put("riderName",    r.riderName);
        m.put("description",  r.description != null ? r.description : "");
        m.put("riderType",    r.riderType != null ? r.riderType : "DISCOUNT");
        m.put("mandatory",    r.mandatory);
        m.put("discountRate", r.discountRate);
        m.put("exclusions",   List.of());
        m.put("provisionId",  null);
        return m;
    }

    private Map<String, Object> coverageToMap(CoverageVO c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",               c.coverageId);
        m.put("coverageId",       c.coverageId);
        m.put("name",             c.coverageName);
        m.put("coverageName",     c.coverageName);
        m.put("coverageType",     c.coverageType);
        m.put("mandatory",        c.mandatory);
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
        m.put("limitOptions", c.limitOptions != null
            ? c.limitOptions.stream().map(o -> Map.of(
                "id",         (Object) o.optionId,
                "optionName", o.optionName != null ? o.optionName : ""
              )).collect(Collectors.toList())
            : List.of());
        return m;
    }
}
