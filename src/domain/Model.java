package domain;

import java.util.Date;

public class Model {
    private int engineCC;
    private FuelType fuelType;
    private String manufacturer;
    private String modelId;
    private String modelName;
    private ModelType modelType;
    private Date modelYear;

    public enum FuelType {}
    public enum ModelType {}

    public int getEngineCC() { return engineCC; }
    public FuelType getFuelType() { return fuelType; }
    public String getManufacturer() { return manufacturer; }
    public String getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public ModelType getModelType() { return modelType; }
    public Date getModelYear() { return modelYear; }
}
