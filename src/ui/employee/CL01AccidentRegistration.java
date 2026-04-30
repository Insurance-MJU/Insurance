package ui.employee;

import ui.StubUseCase;

public class CL01AccidentRegistration {
    public void run() {
        new StubUseCase("CL-01", "사고를 접수한다").run();
    }
}
