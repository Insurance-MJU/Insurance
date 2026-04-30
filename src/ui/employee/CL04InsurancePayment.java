package ui.employee;

import ui.StubUseCase;

public class CL04InsurancePayment {
    public void run() {
        new StubUseCase("CL-04", "보험금을 지급한다").run();
    }
}
