package infra.repository;

import domain.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CarRepository {
    private static final Map<String, Car> STORE = new HashMap<>();

    static {
        // 64마0866 - 현대 그랜저
        Model granjer = new Model();
        granjer.setModelId("MDL-001");
        granjer.setManufacturer("현대");
        granjer.setModelName("그랜저");
        granjer.setModelType(Model.ModelType.LARGE);
        granjer.setFuelType(Model.FuelType.GASOLINE);
        granjer.setEngineCC(2999);
        Calendar c1 = Calendar.getInstance();
        c1.set(2020, Calendar.JANUARY, 1);
        granjer.setModelYear(c1.getTime());

        Car car1 = new Car();
        car1.setCarId("CAR-001");
        car1.setCarNumber("64마0866");
        car1.setOwner("박수현");
        car1.setModel(granjer);

        DriverScope scope1 = new DriverScope();
        scope1.setMinAge(20);
        scope1.setScopeType(DriverScope.ScopeType.SELF);
        car1.setDriverScope(scope1);
        STORE.put("64마0866", car1);

        // 12가3456 - 기아 K5
        Model k5 = new Model();
        k5.setModelId("MDL-002");
        k5.setManufacturer("기아");
        k5.setModelName("K5");
        k5.setModelType(Model.ModelType.MIDSIZE);
        k5.setFuelType(Model.FuelType.GASOLINE);
        k5.setEngineCC(1999);
        Calendar c2 = Calendar.getInstance();
        c2.set(2022, Calendar.JANUARY, 1);
        k5.setModelYear(c2.getTime());

        Car car2 = new Car();
        car2.setCarId("CAR-002");
        car2.setCarNumber("12가3456");
        car2.setOwner("김직원");
        car2.setModel(k5);

        DriverScope scope2 = new DriverScope();
        scope2.setMinAge(20);
        scope2.setScopeType(DriverScope.ScopeType.SELF);
        car2.setDriverScope(scope2);
        STORE.put("12가3456", car2);
    }

    // 외부 DMV 조회 시뮬레이션
    public Car findByCarNumber(String carNumber) {
        return STORE.get(carNumber);
    }

    // 안전장치 정보 (별도 DB 조회 시뮬레이션)
    public String getSafetyDevices(String carNumber) {
        if ("64마0866".equals(carNumber)) return "블랙박스, ABS";
        if ("12가3456".equals(carNumber)) return "ABS";
        return "없음";
    }

    // 기준가액 조회
    public long getStandardValue(String carNumber) {
        if ("64마0866".equals(carNumber)) return 31_635_913L;
        if ("12가3456".equals(carNumber)) return 25_000_000L;
        return 0L;
    }
}
