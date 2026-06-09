package controller.web;

import domain.ProvisionItem;
import domain.ProvisionList;
import domain.StandardProvisions;
import infra.web.Router;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProvisionController {

    private final ProvisionList provisionList;

    public ProvisionController(ProvisionList provisionList) {
        this.provisionList = provisionList;
    }

    public void registerRoutes(Router router) {
        router.get("/master/provisions",
                (req, res) -> res.ok(Map.of("data", provisionList.findAllAsMap())));
        router.get("/master/provisions/{id}",
                (req, res) -> res.ok(provisionList.findByIdAsMap(req.pathVariable("id"))));
        router.post("/master/provisions",
                (req, res) -> { int id = provisionList.save(StandardProvisions.fromMap(req.body(LinkedHashMap.class))); res.created(Map.of("id", id)); });
        router.delete("/master/provisions/{id}",
                (req, res) -> { provisionList.delete(req.pathVariable("id")); res.noContent(); });
        router.get("/master/provisions/{id}/items",
                (req, res) -> res.ok(Map.of("data", provisionList.findItemsAsMap(req.pathVariable("id")))));
        router.post("/master/provisions/{id}/items",
                (req, res) -> { int itemId = provisionList.saveItem(req.pathVariable("id"), ProvisionItem.fromMap(req.body(LinkedHashMap.class))); res.created(Map.of("id", itemId)); });
        router.delete("/master/provisions/items/{id}",
                (req, res) -> { provisionList.deleteItem(req.pathVariable("id")); res.noContent(); });
    }
}
