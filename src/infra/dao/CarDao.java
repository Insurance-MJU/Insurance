package infra.dao;

import domain.Car;
import infra.external.CarClient;

public class CarDao {
    private static final CarDao INSTANCE = new CarDao();
    public static CarDao getInstance() { return INSTANCE; }

    private final CarClient client = CarClient.getInstance();

    public Car findByCarNumber(String carNumber)    { return client.findByCarNumber(carNumber); }
    public String getSafetyDevices(String carNumber) { return client.getSafetyDevices(carNumber); }
    public long getStandardValue(String carNumber)  { return client.getStandardValue(carNumber); }
}
