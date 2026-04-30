package domain;

public class Car extends Insured {
    private String carId;
    private String carNumber;
    private String driver;
    private DriverScope driverScope;
    private Model model;
    private String owner;
    private Purpose purpose;

    public enum Purpose { COMMUTE, BUSINESS, COMMERCIAL }

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
}
