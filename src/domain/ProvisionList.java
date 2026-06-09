package domain;

import infra.dao.ProvisionDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProvisionList {
    private final ProvisionDao dao;

    public ProvisionList(ProvisionDao dao) {
        this.dao = dao;
    }

    public List<Map<String, Object>> findAllAsMap() {
        return dao.findAll().stream()
                .map(StandardProvisions::fromMap)
                .map(StandardProvisions::toMap)
                .collect(Collectors.toList());
    }

    public Map<String, Object> findByIdAsMap(String id) {
        return StandardProvisions.fromMap(dao.findById(id)).toMap();
    }

    public int save(StandardProvisions provision) {
        return dao.save(provision.toMap());
    }

    public void delete(String id) {
        dao.delete(id);
    }

    public List<Map<String, Object>> findItemsAsMap(String provisionId) {
        return dao.findItems(provisionId).stream()
                .map(ProvisionItem::fromMap)
                .map(ProvisionItem::toMap)
                .collect(Collectors.toList());
    }

    public int saveItem(String provisionId, ProvisionItem item) {
        return dao.saveItem(provisionId, item.toMap());
    }

    public void deleteItem(String id) {
        dao.deleteItem(id);
    }
}
