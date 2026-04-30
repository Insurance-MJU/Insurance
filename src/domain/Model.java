package domain;

import java.util.Date;

public class Model {
    private String modelId;
    private String modelName;
    private ModelType modelType;
    private Date modelYear;
    private int engineCC;
    private FuelType fuelType;
    private String manufacturer;

    public enum FuelType {}
    public enum ModelType {}

    public String getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public ModelType getModelType() { return modelType; }
    public Date getModelYear() { return modelYear; }
    public int getEngineCC() { return engineCC; }
    public FuelType getFuelType() { return fuelType; }
    public String getManufacturer() { return manufacturer; }
}
