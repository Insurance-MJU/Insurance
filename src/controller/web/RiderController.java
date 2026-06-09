package controller.web;

import domain.RiderList;
import infra.web.Router;

import java.util.LinkedHashMap;
import java.util.Map;

public class RiderController {

    private final RiderList riderList;

    public RiderController(RiderList riderList) {
        this.riderList = riderList;
    }

    public void registerRoutes(Router router) {
        router.get("/master/riders/{id}",
                (req, res) -> res.ok(riderList.findByCodeAsMap(req.pathVariable("id"))));
        router.post("/master/riders",
                (req, res) -> { riderList.saveNew(RiderList.fromMap(req.body(LinkedHashMap.class))); res.created(Map.of("ok", true)); });
        router.put("/master/riders/{id}",
                (req, res) -> { riderList.updateByCode(req.pathVariable("id"), RiderList.fromMap(req.body(LinkedHashMap.class))); res.ok(Map.of("ok", true)); });
        router.delete("/master/riders/{id}",
                (req, res) -> { riderList.deleteByCode(req.pathVariable("id")); res.noContent(); });
    }
}
