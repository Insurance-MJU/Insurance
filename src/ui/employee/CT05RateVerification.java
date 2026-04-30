package ui.employee;

import ui.StubUseCase;

public class CT05RateVerification {
    public void run() {
        new StubUseCase("CT-05", "요율검증을 요청한다").run();
    }
}
