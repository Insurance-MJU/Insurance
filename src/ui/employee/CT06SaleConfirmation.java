package ui.employee;

import ui.StubUseCase;

public class CT06SaleConfirmation {
    public void run() {
        new StubUseCase("CT-06", "상품판매를 확정한다").run();
    }
}
