package ui.customer;

import ui.StubUseCase;

public class CS04ClaimRequest {
    public void run() {
        new StubUseCase("CS-04", "보험금을 청구한다").run();
    }
}
