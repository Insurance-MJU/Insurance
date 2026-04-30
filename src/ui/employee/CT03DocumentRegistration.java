package ui.employee;

import ui.StubUseCase;

public class CT03DocumentRegistration {
    public void run() {
        new StubUseCase("CT-03", "기초서류를 등록한다").run();
    }
}
