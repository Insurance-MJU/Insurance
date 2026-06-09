package controller.web;

import domain.Exclusion;
import domain.ExclusionList;
import infra.web.Router;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExclusionController {

    private final ExclusionList exclusionList;

    public ExclusionController(ExclusionList exclusionList) {
        this.exclusionList = exclusionList;
    }

    public void registerRoutes(Router router) {
        router.get("/master/exclusions",
                (req, res) -> res.ok(Map.of("data", exclusionList.findAllAsMap())));
        router.post("/master/exclusions",
                (req, res) -> { int id = exclusionList.save(Exclusion.fromMap(req.body(LinkedHashMap.class))); res.created(Map.of("id", id)); });
        router.delete("/master/exclusions/{id}",
                (req, res) -> { exclusionList.delete(req.pathVariable("id")); res.noContent(); });
    }
}
