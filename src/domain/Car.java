package domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Car extends Insured {
    private String carId;
    private String carNumber;
    private String driver;
    private DriverScope driverScope;
    private Model model;
    private String owner;
    private Purpose purpose;

    public enum Purpose { COMMUTE, BUSINESS, COMMERCIAL }

    // ── 비즈니스 메서드 ───────────────────────────────────────
    public void changePurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public boolean isDriverAllowed(int age) {
        if (driverScope == null) return true;
        return driverScope.allowsAge(age);
    }

    public String getPurposeLabel() {
        if (purpose == Purpose.COMMUTE)    return "출퇴근/가정용";
        if (purpose == Purpose.BUSINESS)   return "업무용";
        if (purpose == Purpose.COMMERCIAL) return "영업용";
        return "";
    }

    // Setters
    public void setCarId(String v)          { this.carId = v; }
    public void setCarNumber(String v)      { this.carNumber = v; }
    public void setDriver(String v)         { this.driver = v; }
    public void setDriverScope(DriverScope v){ this.driverScope = v; }
    public void setModel(Model v)           { this.model = v; }
    public void setOwner(String v)          { this.owner = v; }
    public void setPurpose(Purpose v)       { this.purpose = v; }

    // Getters
    public String getCarId()          { return carId; }
    public String getCarNumber()      { return carNumber; }
    public String getDriver()         { return driver; }
    public DriverScope getDriverScope(){ return driverScope; }
    public Model getModel()           { return model; }
    public String getOwner()          { return owner; }
    public Purpose getPurpose()       { return purpose; }

    // ── 영속성 ────────────────────────────────────────────────
    private static final Map<String, Car> STORE = new HashMap<>();
    static {
        Model granjer = new Model();
        granjer.setModelId("MDL-001"); granjer.setManufacturer("현대"); granjer.setModelName("그랜저");
        granjer.setModelType(Model.ModelType.LARGE); granjer.setFuelType(Model.FuelType.GASOLINE);
        granjer.setEngineCC(2999);
        Calendar c1 = Calendar.getInstance(); c1.set(2020, Calendar.JANUARY, 1);
        granjer.setModelYear(c1.getTime());

        Car car1 = new Car();
        car1.setCarId("CAR-001"); car1.setCarNumber("64마0866"); car1.setOwner("박수현");
        car1.setModel(granjer);
        DriverScope scope1 = new DriverScope();
        scope1.setMinAge(20); scope1.setScopeType(DriverScope.ScopeType.SELF);
        car1.setDriverScope(scope1);
        STORE.put("64마0866", car1);

        Model k5 = new Model();
        k5.setModelId("MDL-002"); k5.setManufacturer("기아"); k5.setModelName("K5");
        k5.setModelType(Model.ModelType.MIDSIZE); k5.setFuelType(Model.FuelType.GASOLINE);
        k5.setEngineCC(1999);
        Calendar c2 = Calendar.getInstance(); c2.set(2022, Calendar.JANUARY, 1);
        k5.setModelYear(c2.getTime());

        Car car2 = new Car();
        car2.setCarId("CAR-002"); car2.setCarNumber("12가3456"); car2.setOwner("김직원");
        car2.setModel(k5);
        DriverScope scope2 = new DriverScope();
        scope2.setMinAge(20); scope2.setScopeType(DriverScope.ScopeType.SELF);
        car2.setDriverScope(scope2);
        STORE.put("12가3456", car2);
    }

    public static Car findByCarNumber(String carNumber) { return STORE.get(carNumber); }

    public static String getSafetyDevices(String carNumber) {
        if ("64마0866".equals(carNumber)) return "블랙박스, ABS";
        if ("12가3456".equals(carNumber)) return "ABS";
        return "없음";
    }

    public static long getStandardValue(String carNumber) {
        if ("64마0866".equals(carNumber)) return 31_635_913L;
        if ("12가3456".equals(carNumber)) return 25_000_000L;
        return 0L;
    }
}
