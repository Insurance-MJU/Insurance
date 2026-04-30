package infra;

import domain.Rider;
import java.util.HashMap;
import java.util.Map;

public class RiderRepository {
    private static final Map<String, Rider> STORE = new HashMap<>();

    static {
        Rider mileage = new Rider();
        mileage.setRiderId("RIDER-001");
        mileage.setRiderCode("RC-MILEAGE");
        mileage.setRiderName("마일리지 특약");
        mileage.setRiderType(Rider.RiderType.MILEAGE);
        mileage.setDescription(
            "연간환산 운행거리 15,000 km 이하 주행 시, 운행거리 실적에 따라 " +
            "보험료를 환급 받을 수 있는 특약입니다.\n" +
            " - 15,000km 이하: 최대 30% 환급\n" +
            " - 10,000km 이하: 최대 50% 환급\n" +
            " - 5,000km 이하 : 최대 70% 환급"
        );
        mileage.setMandatory(false);
        STORE.put("RC-MILEAGE", mileage);

        Rider tmap = new Rider();
        tmap.setRiderId("RIDER-002");
        tmap.setRiderCode("RC-TMAP");
        tmap.setRiderName("티맵안전운전 할인특약");
        tmap.setRiderType(Rider.RiderType.SAFETY);
        tmap.setDescription(
            "티맵(T map) 앱을 통해 안전운전 점수를 측정하여 점수에 따라 " +
            "보험료를 할인받을 수 있는 특약입니다.\n" +
            " - 90점 이상: 10% 할인\n" +
            " - 80점 이상: 7% 할인\n" +
            " - 70점 이상: 5% 할인"
        );
        tmap.setDiscountRate(0.10);
        tmap.setMandatory(false);
        STORE.put("RC-TMAP", tmap);
    }

    public Rider findByCode(String riderCode) {
        return STORE.get(riderCode);
    }
}
