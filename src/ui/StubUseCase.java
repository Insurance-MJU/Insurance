package ui;

import infra.Context;
import java.util.Scanner;

public class StubUseCase {
    private final String id;
    private final String name;
    private final Scanner sc = Context.getInstance().scanner();

    public StubUseCase(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void run() {
        System.out.println("\n[" + id + "] " + name);
        System.out.println("(해당 기능은 아직 구현되지 않았습니다.)");
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
