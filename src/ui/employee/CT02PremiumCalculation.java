package ui.employee;

import ui.StubUseCase;

public class CT02PremiumCalculation {
    public void run() {
        new StubUseCase("CT-02", "보험료를 산출한다").run();
    }
}
