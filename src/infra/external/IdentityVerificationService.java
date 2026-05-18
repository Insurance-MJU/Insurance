package infra.external;

import java.util.Scanner;

/**
 * 외부 본인인증 시스템 연동 스텁.
 * 실제 환경에서는 공동인증서(금결원), 휴대폰 인증(PASS) 등 외부 API를 호출한다.
 */
public class IdentityVerificationService {

    public static class AuthResult {
        public final String name;
        public final String ssn;
        public final String phone;

        AuthResult(String name, String ssn, String phone) {
            this.name  = name;
            this.ssn   = ssn;
            this.phone = phone;
        }
    }

    private final Scanner sc;

    public IdentityVerificationService(Scanner sc) {
        this.sc = sc;
    }

    /**
     * 본인인증 수행. 인증 수단 미선택 시 재시도.
     * @return AuthResult (성공) 또는 null (취소)
     */
    public AuthResult verify() {
        System.out.println("\n[본인 인증]");
        System.out.println(" 1. 공동인증서  2. 간편비밀번호  3. 휴대폰 인증");
        System.out.print(" 인증 수단 선택: ");
        String method = sc.nextLine().trim();

        while (!method.equals("1") && !method.equals("2") && !method.equals("3")) {
            System.out.println("[경고] 인증 수단을 선택해 주세요 (1~3).");
            System.out.print(" 인증 수단 선택: ");
            method = sc.nextLine().trim();
        }

        System.out.print(" 이름: ");
        String name = sc.nextLine().trim();
        System.out.print(" 주민등록번호 (예: 020101-3******): ");
        String ssn = sc.nextLine().trim();
        System.out.print(" 휴대전화번호 (예: 010-1234-5678): ");
        String phone = sc.nextLine().trim();
        System.out.print(" 인증번호 (예: 123456): ");
        sc.nextLine();

        // 외부 시스템 응답 시뮬레이션
        System.out.println("\n[본인 인증 결과] 인증이 완료되었습니다. 고객명: " + name);
        return new AuthResult(name, ssn, phone);
    }
}
