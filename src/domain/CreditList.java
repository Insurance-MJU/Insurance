package domain;

import infra.dao.CreditDao;

public class CreditList {
    private final CreditDao dao;

    public CreditList(CreditDao dao) {
        this.dao = dao;
    }

    public CreditInfo findByApplicant(String ssn, String carNumber) {
        return dao.findByApplicant(ssn, carNumber);
    }
}
