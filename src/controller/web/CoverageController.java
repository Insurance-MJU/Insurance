package controller.web;

import domain.Coverage;
import domain.CoverageList;
import infra.web.Router;

import java.util.LinkedHashMap;
import java.util.Map;

public class CoverageController {

    private final CoverageList coverageList;

    public CoverageController(CoverageList coverageList) {
        this.coverageList = coverageList;
    }

    public void registerRoutes(Router router) {
        router.get("/master/coverages/{id}",
                (req, res) -> res.ok(coverageList.findByIdAsMap(req.pathVariable("id"))));
        router.post("/master/coverages",
                (req, res) -> { coverageList.saveNew(CoverageList.fromMap(req.body(LinkedHashMap.class))); res.created(Map.of("ok", true)); });
        router.put("/master/coverages/{id}",
                (req, res) -> { coverageList.updateById(req.pathVariable("id"), CoverageList.fromMap(req.body(LinkedHashMap.class))); res.ok(Map.of("ok", true)); });
        router.delete("/master/coverages/{id}",
                (req, res) -> { coverageList.delete(req.pathVariable("id")); res.noContent(); });
    }
}
