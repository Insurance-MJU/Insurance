package ui.customer;

import infra.Context;
import java.util.Scanner;

public class CS01ProductSubscription {
    private final Scanner sc = Context.getInstance().scanner();

    private static final String[][] CAR_DB = {
        {"64마0866", "현대", "그랜저", "대형", "가솔린", "2,999", "2020", "31,635,913", "블랙박스, ABS"},
        {"12가3456", "기아", "K5",    "중형", "가솔린", "1,999", "2022", "25,000,000", "ABS"}
    };

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-01: 상품가입을 요청한다");
        System.out.println("========================================");

        // <<include>> CS-02: 상품 조회
        System.out.println("\n[상품 정보]");
        System.out.println(" 상품명    : MZ 세대 다이렉트 자동차보험");
        System.out.println(" 상품코드  : CAR-2026-MZ");
        System.out.println(" 보험사    : 한국자동차보험");
        System.out.println(" 판매기간  : 2026-05-01 ~ 2027-04-30");
        System.out.println(" 가입대상  : 만 20세 이상 39세 이하");

        System.out.println("\n[본인 확인]");
        System.out.print(" 이름: ");
        String name = sc.nextLine().trim();

        System.out.print(" 주민등록번호 (예: 020101-3******): ");
        String ssn = sc.nextLine().trim();

        // E1: 나이 조건 검사
        int age = parseAge(ssn);
        if (age != -1 && (age < 20 || age > 39)) {
            System.out.println("\n[오류] 해당 상품의 가입 대상 연령 조건에 해당하지 않아 가입이 제한됩니다.");
            returnToMenu();
            return;
        }

        System.out.print(" 전화번호 (예: 010-1234-5678): ");
        String phone = sc.nextLine().trim();

        System.out.println("\n[개인정보 처리 동의]");
        System.out.println(" (필수) 개인정보 수집·이용 동의");
        System.out.println(" (선택) 마케팅 정보 수신 동의");
        System.out.print(" 필수 항목에 동의하십니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 필수 동의 항목에 동의하셔야 진행이 가능합니다.");
            returnToMenu();
            return;
        }

        // 차량 조회
        System.out.println("\n[차량 정보 조회]");
        String[] car = null;
        while (car == null) {
            System.out.print(" 차량번호를 입력하세요: ");
            String carNo = sc.nextLine().trim();
            car = findCar(carNo);
            if (car == null) {
                // A1: 차량 정보 없음
                System.out.println("[경고] 입력하신 차량번호로 차량 정보를 조회할 수 없습니다. 차량번호를 확인해 주세요.");
                System.out.print(" 다시 입력하시겠습니까? (Y/N): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                    returnToMenu();
                    return;
                }
            }
        }

        System.out.println("\n[조회된 차량 정보]");
        System.out.printf(" 차량번호    : %s%n",   car[0]);
        System.out.printf(" 제조사      : %s%n",   car[1]);
        System.out.printf(" 모델명      : %s%n",   car[2]);
        System.out.printf(" 차종        : %s%n",   car[3]);
        System.out.printf(" 연료        : %s%n",   car[4]);
        System.out.printf(" 배기량      : %scc%n",  car[5]);
        System.out.printf(" 연식        : %s%n",   car[6]);
        System.out.printf(" 차량기준가액 : %s원%n", car[7]);
        System.out.printf(" 안전장치    : %s%n",   car[8]);

        System.out.print("\n계속 진행하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            returnToMenu();
            return;
        }

        System.out.println("\n[운행 정보 입력]");
        System.out.println(" 운행 용도:");
        System.out.println("  1. 출퇴근/가정용");
        System.out.println("  2. 영업용");
        System.out.println("  3. 업무용");
        System.out.print(" 선택: ");
        String purposeStr = sc.nextLine().trim();
        String purpose;
        if ("2".equals(purposeStr))      purpose = "영업용";
        else if ("3".equals(purposeStr)) purpose = "업무용";
        else                             purpose = "출퇴근/가정용";

        System.out.println("\n 운전자 범위:");
        System.out.println("  1. 본인한정");
        System.out.println("  2. 가족한정");
        System.out.print(" 선택: ");
        String scopeStr = sc.nextLine().trim();
        String driverScope = "본인한정";

        if ("2".equals(scopeStr)) {
            // A2: 가족한정 → 가족 정보 입력
            driverScope = "가족한정";
            System.out.println("\n[가족 정보 입력]");
            System.out.print(" 이름: ");
            String familyName = sc.nextLine().trim();
            System.out.print(" 관계 (예: 동생, 배우자): ");
            String relation = sc.nextLine().trim();
            System.out.print(" 생년월일 (예: 070101): ");
            String birthDate = sc.nextLine().trim();
            System.out.println(" → 가족 정보 등록: " + familyName + " / " + relation + " / " + birthDate);
        }

        // <<include>> CS-03: 예상보험료 산출
        System.out.println("\n[예상 보험료 산출]");
        int base    = 1150000;
        int premium = 460000;
        System.out.printf(" 기본 보험료   : %,d원%n", base);
        System.out.printf(" 할인 적용 후  : %,d원  (다이렉트 60%% 할인)%n", premium);

        System.out.print("\n위 내용으로 가입을 신청하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 가입이 취소되었습니다.");
            returnToMenu();
            return;
        }

        // E2: 자동심사 (영업용은 거절)
        if (!passUnderwriting(purpose)) {
            System.out.println("\n[거절] 가입이 거절되었습니다. 자세한 사항은 고객 센터로 연락주세요.(1588-1000)");
            returnToMenu();
            return;
        }

        System.out.println("\n보험가입이 완료되었습니다.");
        System.out.printf(" 가입자      : %s%n",      name);
        System.out.printf(" 전화번호    : %s%n",      phone);
        System.out.printf(" 차량번호    : %s%n",      car[0]);
        System.out.printf(" 운행용도    : %s%n",      purpose);
        System.out.printf(" 운전자범위  : %s%n",      driverScope);
        System.out.printf(" 납입 보험료 : %,d원/년%n", premium);
        returnToMenu();
    }

    private String[] findCar(String carNo) {
        for (String[] car : CAR_DB) {
            if (car[0].equals(carNo)) return car;
        }
        return null;
    }

    private int parseAge(String ssn) {
        try {
            String[] parts = ssn.split("-");
            if (parts.length < 2 || parts[0].length() < 2) return -1;
            int yy = Integer.parseInt(parts[0].substring(0, 2));
            char g = parts[1].charAt(0);
            int birthYear = (g == '1' || g == '2') ? 1900 + yy : 2000 + yy;
            return 2026 - birthYear;
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean passUnderwriting(String purpose) {
        return !"영업용".equals(purpose);
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
