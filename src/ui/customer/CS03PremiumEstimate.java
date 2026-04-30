package ui.customer;

import ui.StubUseCase;

public class CS03PremiumEstimate {
    public void run() {
        new StubUseCase("CS-03", "예상보험료를 산출한다").run();
    }
}
