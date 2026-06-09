package domain;

import infra.dao.ExclusionDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExclusionList {
    private final ExclusionDao dao;

    public ExclusionList(ExclusionDao dao) {
        this.dao = dao;
    }

    public List<Map<String, Object>> findAllAsMap() {
        return dao.findAll().stream()
                .map(Exclusion::fromMap)
                .map(Exclusion::toMap)
                .collect(Collectors.toList());
    }

    public int save(Exclusion exclusion) {
        return dao.save(exclusion.toMap());
    }

    public void delete(String id) {
        dao.delete(id);
    }
}
