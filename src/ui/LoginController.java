package ui;

import domain.common.User;
import infra.Context;
import infra.repository.UserRepository;
import java.util.Scanner;

public class LoginController {
    private final UserRepository userRepo = new UserRepository();
    private final Scanner sc = Context.getInstance().scanner();

    public void run() {
        System.out.println("========================================");
        System.out.println("     자동차 보험 상품 관리 시스템");
        System.out.println("========================================");

        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("아이디: ");
            String userId = sc.nextLine().trim();
            System.out.print("비밀번호: ");
            String password = sc.nextLine().trim();

            User user = userRepo.findByCredentials(userId, password);
            if (user != null) {
                Context.getInstance().login(user);
                System.out.println("\n안녕하세요, " + user.getName() + "님!\n");
                return;
            }

            System.out.println("[오류] 아이디 또는 비밀번호가 올바르지 않습니다. (" + attempt + "/3)\n");
        }

        System.out.println("로그인 시도 횟수를 초과했습니다. 시스템을 종료합니다.");
    }
}
