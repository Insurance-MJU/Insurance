package ui.employee;

import ui.StubUseCase;

public class UW01ContractReview {
    public void run() {
        new StubUseCase("UW-01", "계약인수를 심사한다").run();
    }
}
