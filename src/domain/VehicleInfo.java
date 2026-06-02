package domain;

public class VehicleInfo {
    private final String carNumber;
    private final String manufacturer;
    private final String modelName;
    private final String modelType;
    private final int modelYear;
    private final int engineCC;
    private final String fuelType;
    private final long standardValue;
    private final boolean hasABS;
    private final boolean hasBlackbox;
    private final String errorReason;

    public VehicleInfo(String carNumber, String manufacturer, String modelName, String modelType,
                       int modelYear, int engineCC, String fuelType, long standardValue,
                       boolean hasABS, boolean hasBlackbox) {
        this.carNumber = carNumber;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.modelType = modelType;
        this.modelYear = modelYear;
        this.engineCC = engineCC;
        this.fuelType = fuelType;
        this.standardValue = standardValue;
        this.hasABS = hasABS;
        this.hasBlackbox = hasBlackbox;
        this.errorReason = null;
    }

    private VehicleInfo(String carNumber, String errorReason) {
        this.carNumber = carNumber;
        this.manufacturer = null;
        this.modelName = null;
        this.modelType = null;
        this.modelYear = 0;
        this.engineCC = 0;
        this.fuelType = null;
        this.standardValue = 0;
        this.hasABS = false;
        this.hasBlackbox = false;
        this.errorReason = errorReason;
    }

    public static VehicleInfo notFound(String carNumber, String reason) {
        return new VehicleInfo(carNumber, reason);
    }

    public boolean isFound()      { return errorReason == null; }
    public String  getCarNumber() { return carNumber; }
    public String  getManufacturer() { return manufacturer; }
    public String  getModelName() { return modelName; }
    public String  getModelType() { return modelType; }
    public int     getModelYear() { return modelYear; }
    public int     getEngineCC()  { return engineCC; }
    public String  getFuelType()  { return fuelType; }
    public long    getStandardValue() { return standardValue; }
    public boolean hasABS()       { return hasABS; }
    public boolean hasBlackbox()  { return hasBlackbox; }
    public String  getErrorReason() { return errorReason; }
}
