package controller.web;

import domain.BaseRate;
import domain.BaseRateList;
import infra.web.Router;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseRateController {

    private final BaseRateList baseRateList;

    public BaseRateController(BaseRateList baseRateList) {
        this.baseRateList = baseRateList;
    }

    public void registerRoutes(Router router) {
        router.get("/master/base-rates",
                (req, res) -> res.ok(Map.of("data", baseRateList.findAllAsMap(req.queryParam("type")))));
        router.post("/master/base-rates",
                (req, res) -> { baseRateList.save(BaseRate.fromMap(req.body(LinkedHashMap.class))); res.ok(Map.of("ok", true)); });
        router.delete("/master/base-rates/{id}",
                (req, res) -> { baseRateList.delete(req.pathVariable("id")); res.noContent(); });
        router.get("/master/base-rates/stats",
                (req, res) -> res.ok(Map.of("data", baseRateList.findStatsAsMap(req.queryParam("type")))));
    }
}
