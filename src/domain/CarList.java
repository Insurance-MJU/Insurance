package domain;

import infra.dao.CarDao;

public class CarList {
    private final CarDao dao;

    public CarList(CarDao dao) {
        this.dao = dao;
    }

    public VehicleInfo findByCarNumber(String carNumber) {
        return dao.findByCarNumber(carNumber);
    }
}
