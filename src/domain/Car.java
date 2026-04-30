package domain;

public class Car extends Insured {
    private String carId;
    private String carNumber;
    private String driver;
    private DriverScope driverScope;
    private Model model;
    private String owner;
    private Purpose purpose;

    public enum Purpose {}

    public boolean isDriverAllowed(int age) {
        if (driverScope == null) return true;
        return driverScope.allowsAge(age);
    }

    public String getCarId() { return carId; }
    public String getCarNumber() { return carNumber; }
    public String getDriver() { return driver; }
    public DriverScope getDriverScope() { return driverScope; }
    public Model getModel() { return model; }
    public String getOwner() { return owner; }
    public Purpose getPurpose() { return purpose; }
}
