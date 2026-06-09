package domain;

import infra.dao.BaseRateDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseRateList {
    private final BaseRateDao dao;

    public BaseRateList(BaseRateDao dao) {
        this.dao = dao;
    }

    public List<Map<String, Object>> findAllAsMap(String type) {
        return dao.findAll(type).stream()
                .map(BaseRate::fromMap)
                .map(BaseRate::toMap)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> findStatsAsMap(String type) {
        return dao.findStats(type).stream()
                .map(BaseRateStat::fromMap)
                .map(BaseRateStat::toMap)
                .collect(Collectors.toList());
    }

    public void save(BaseRate rate) {
        dao.save(rate.toMap());
    }

    public void delete(String id) {
        dao.delete(id);
    }
}
