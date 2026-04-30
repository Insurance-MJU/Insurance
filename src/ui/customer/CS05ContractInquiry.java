package ui.customer;

import ui.StubUseCase;

public class CS05ContractInquiry {
    public void run() {
        new StubUseCase("CS-05", "보험계약을 조회한다").run();
    }
}
