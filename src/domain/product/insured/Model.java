package domain.product.insured;

import java.util.Date;

public class Model {
    private String modelId;
    private String modelName;
    private ModelType modelType;
    private Date modelYear;
    private int engineCC;
    private FuelType fuelType;
    private String manufacturer;

    public enum FuelType    { GASOLINE, DIESEL, HYBRID, ELECTRIC, LPG }
    public enum ModelType   { COMPACT, MIDSIZE, LARGE, SUV, VAN, TRUCK }

    public String getFuelLabel() {
        if (fuelType == FuelType.GASOLINE) return "가솔린";
        if (fuelType == FuelType.DIESEL)   return "디젤";
        if (fuelType == FuelType.HYBRID)   return "하이브리드";
        if (fuelType == FuelType.ELECTRIC) return "전기";
        if (fuelType == FuelType.LPG)      return "LPG";
        return "";
    }

    public String getTypeLabel() {
        if (modelType == ModelType.COMPACT) return "소형";
        if (modelType == ModelType.MIDSIZE) return "중형";
        if (modelType == ModelType.LARGE)   return "대형";
        if (modelType == ModelType.SUV)     return "SUV";
        if (modelType == ModelType.VAN)     return "승합";
        if (modelType == ModelType.TRUCK)   return "트럭";
        return "";
    }

    // Setters
    public void setModelId(String v)       { this.modelId = v; }
    public void setModelName(String v)     { this.modelName = v; }
    public void setModelType(ModelType v)  { this.modelType = v; }
    public void setModelYear(Date v)       { this.modelYear = v; }
    public void setEngineCC(int v)         { this.engineCC = v; }
    public void setFuelType(FuelType v)    { this.fuelType = v; }
    public void setManufacturer(String v)  { this.manufacturer = v; }

    // Getters
    public String getModelId()       { return modelId; }
    public String getModelName()     { return modelName; }
    public ModelType getModelType()  { return modelType; }
    public Date getModelYear()       { return modelYear; }
    public int getEngineCC()         { return engineCC; }
    public FuelType getFuelType()    { return fuelType; }
    public String getManufacturer()  { return manufacturer; }
}
