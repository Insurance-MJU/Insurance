package ui.employee;

import ui.StubUseCase;

public class CT04ProductApproval {
    public void run() {
        new StubUseCase("CT-04", "상품인가를 신청한다").run();
    }
}
