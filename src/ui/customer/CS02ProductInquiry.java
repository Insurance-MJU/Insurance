package ui.customer;

import ui.StubUseCase;

public class CS02ProductInquiry {
    public void run() {
        new StubUseCase("CS-02", "보험상품을 조회한다").run();
    }
}
