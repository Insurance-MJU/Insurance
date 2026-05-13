package ui.customer;

import domain.*;
import infra.repository.CarRepository;
import infra.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CS03PremiumEstimate {
    private final Scanner sc = Context.getInstance().scanner();
    private final CarRepository carRepo = new CarRepository();

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-03: 예상보험료를 산출한다");
        System.out.println("========================================");

        Product product = new CS02ProductInquiry().run();
        if (product == null) { returnToMenu(); return; }

        Car car = null;
        long stdValue = 0;
        while (car == null) {
            System.out.println("\n[차량 정보 조회]");
            System.out.print(" 차량번호를 입력하세요: ");
            String carNo = sc.nextLine().trim();
            car = carRepo.findByCarNumber(carNo);
            if (car == null) {
                System.out.println("[경고] 입력하신 차량번호로 차량 정보를 조회할 수 없습니다.");
                System.out.print(" 다시 입력하시겠습니까? (Y/N): ");
                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) { returnToMenu(); return; }
            } else {
                stdValue = carRepo.getStandardValue(car.getCarNumber());
            }
        }

        System.out.println("\n[운행 용도 선택]");
        System.out.println(" 1. 출퇴근/가정용  2. 영업용  3. 업무용");
        System.out.print(" 선택: ");
        String p = sc.nextLine().trim();
        Car.Purpose purpose = "2".equals(p) ? Car.Purpose.COMMERCIAL
                            : "3".equals(p) ? Car.Purpose.BUSINESS
                            : Car.Purpose.COMMUTE;

        runAsInclude(product, stdValue, purpose);
        returnToMenu();
    }

    /** @return 최종 보험료(양수) 또는 취소 시 -1 */
    public long runAsInclude(Product product, long stdValue, Car.Purpose purpose) {
        System.out.println("\n========================================");
        System.out.println(" CS-03: 예상보험료를 산출한다");
        System.out.println("========================================");

        // Step 1: 할인 특약 선택
        System.out.println("\n[할인 특약 선택]");
        System.out.println(" 1. 블랙박스 할인특약");
        System.out.println(" 2. 커넥티드카 할인특약");
        System.out.println(" 3. 티맵안전운전 할인특약");
        System.out.println(" 0. 선택 안함");
        System.out.print(" 선택 (쉼표로 복수 선택, 예: 1,3): ");
        String riderChoice = sc.nextLine().trim();

        // A1: 티맵안전운전 할인특약 요건 미충족
        if (riderChoice.contains("3")) {
            System.out.println("\n[안내] 티맵안전운전 할인특약 요건 조회 중...");
            System.out.println("       조회 결과, 할인 적용 기준(티맵 점수 70점 이상)에 미달하여");
            System.out.println("       해당 할인을 적용할 수 없습니다.");
            riderChoice = riderChoice.replace("3", "").replace(",,", ",").replaceAll("^,|,$", "");
        }

        // Step 3~4: 피보험자/계약자 정보 입력
        System.out.println("\n[피보험자/계약자 정보 입력]");
        System.out.print(" 피보험자 이름: ");
        sc.nextLine();
        System.out.print(" 피보험자 주민번호 (예: 020101-3******): ");
        sc.nextLine();
        System.out.print(" 피보험자 연락처: ");
        sc.nextLine();
        System.out.print(" 계약자가 피보험자와 동일합니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.print(" 계약자 이름: ");
            sc.nextLine();
            System.out.print(" 계약자 주민번호: ");
            sc.nextLine();
            System.out.print(" 계약자 연락처: ");
            sc.nextLine();
        }

        // Step 5~6: 할인 적용 항목 선택
        System.out.println("\n[할인 적용 항목 선택]");
        for (PremiumCalculation.DiscountItem item : PremiumCalculation.DiscountItem.values()) {
            System.out.printf(" %d. %s  (할인율: %.1f%%)%n",
                ordinal(item) + 1, item.getLabel(), item.getRate() * 100);
        }
        System.out.println(" 0. 해당 없음");
        System.out.print(" 선택 (쉼표로 복수 선택, 예: 1,2): ");
        String discountChoice = sc.nextLine().trim();

        List<PremiumCalculation.DiscountItem> discounts = new ArrayList<>();
        if (discountChoice.contains("1")) discounts.add(PremiumCalculation.DiscountItem.MILEAGE);
        if (discountChoice.contains("2")) discounts.add(PremiumCalculation.DiscountItem.NO_ACCIDENT_3Y);
        if (discountChoice.contains("3")) discounts.add(PremiumCalculation.DiscountItem.ABS);

        // Step 7: 도메인이 보험료 산출
        PremiumCalculation calc = PremiumCalculation.calculate(stdValue, purpose, discounts);

        // E1: 시스템 내부 오류 (시뮬레이션 - 정상 처리)
        System.out.println("\n[최종 보험료 확인]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" 대인배상I (의무)  : %,d원%n", calc.getPersonalInjuryMandatory());
        System.out.printf(" 대인배상II        : %,d원%n", calc.getPersonalInjuryOptional());
        System.out.printf(" 대물배상          : %,d원%n", calc.getPropertyDamage());
        System.out.printf(" 자동차상해        : %,d원%n", calc.getAutoInjury());
        System.out.printf(" 무보험차상해      : %,d원%n", calc.getUninsuredVehicle());
        System.out.printf(" 자기차량손해      : %,d원%n", calc.getOwnVehicleDamage());
        System.out.printf(" 긴급출동서비스    : %,d원%n", calc.getEmergencyService());
        System.out.printf(" 할인금액          : -%,d원  (%.1f%% 적용)%n",
            calc.getDiscountAmount(), calc.getDiscountRate() * 100);
        System.out.println("------------------------------------------------------------");
        System.out.printf(" ── 최종 보험료    : %,d원/년%n", calc.getFinalPremium());
        System.out.println("------------------------------------------------------------");

        // Step 8: 청약 내용 및 상품설명서 확인
        System.out.println("\n[청약 내용 및 상품설명서]");
        System.out.printf(" 상품명   : %s%n", product.getProductName());
        System.out.printf(" 가입대상 : %s%n", product.getTargetDescription());
        System.out.println(" 보장내용 : 대인/대물/자동차상해/무보험차상해/자기차량손해");
        System.out.print("\n위 내용을 이해하였습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 가입이 취소되었습니다.");
            return -1;
        }
        System.out.print("[가입] 버튼 - 최종 가입을 신청하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 가입이 취소되었습니다.");
            return -1;
        }

        System.out.println("\n보험가입을 신청하는 중입니다...");
        return calc.getFinalPremium();
    }

    private int ordinal(PremiumCalculation.DiscountItem item) {
        PremiumCalculation.DiscountItem[] values = PremiumCalculation.DiscountItem.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == item) return i;
        }
        return 0;
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
