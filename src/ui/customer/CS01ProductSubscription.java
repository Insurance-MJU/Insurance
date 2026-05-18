package ui.customer;

import domain.*;
import domain.common.Money;
import domain.Contract;
import infra.Context;
import infra.external.CarClient;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CS01ProductSubscription {

    private final Scanner sc = Context.getInstance().scanner();
    private final CarClient carClient = new CarClient();

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-01: 상품가입을 요청한다");
        System.out.println("========================================");

        // <<include>> CS-02: 상품 조회
        Product selectedProduct = new CS02ProductInquiry().run();
        if (selectedProduct == null) {
            returnToMenu();
            return;
        }

        // ── Step 1: 본인 확인 ────────────────────────────────
        System.out.println("\n[본인 확인]");
        System.out.print(" 이름: ");
        String name = sc.nextLine().trim();

        System.out.print(" 주민등록번호 (예: 020101-3******): ");
        String ssn = sc.nextLine().trim();

        // E1: 나이 조건 검사 (PERSONAL → 만 20~39세)
        if (selectedProduct.getTarget() == Product.Target.PERSONAL) {
            int age = Party.calcAge(ssn);
            if (age != -1 && (age < 20 || age > 39)) {
                System.out.println("\n[오류] 해당 상품의 가입 대상 연령 조건에 해당하지 않아 가입이 제한됩니다.");
                returnToMenu();
                return;
            }
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

        // ── Step 3-5: 차량 조회 ──────────────────────────────
        System.out.println("\n[차량 정보 조회]");
        Car car = null;
        while (car == null) {
            System.out.print(" 차량번호를 입력하세요: ");
            String carNo = sc.nextLine().trim();
            car = carClient.findByCarNumber(carNo);

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

        Model model = car.getModel();
        String safetyDevices = carClient.getSafetyDevices(car.getCarNumber());
        long stdValue = carClient.getStandardValue(car.getCarNumber());

        System.out.println("\n[조회된 차량 정보]");
        System.out.printf(" 차량번호    : %s%n",      car.getCarNumber());
        System.out.printf(" 제조사      : %s%n",      model.getManufacturer());
        System.out.printf(" 모델명      : %s%n",      model.getModelName());
        System.out.printf(" 차종        : %s%n",      model.getTypeLabel());
        System.out.printf(" 연료        : %s%n",      model.getFuelLabel());
        System.out.printf(" 배기량      : %,dcc%n",   model.getEngineCC());
        System.out.printf(" 연식        : %tY%n",     model.getModelYear());
        System.out.printf(" 차량기준가액 : %,d원%n",  stdValue);
        System.out.printf(" 안전장치    : %s%n",      safetyDevices);

        System.out.print("\n차량 정보를 확인했습니다. 계속 진행하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            returnToMenu();
            return;
        }

        // ── Step 7: 운행 용도 & 운전자 범위 ──────────────────
        System.out.println("\n[운행 정보 입력]");
        System.out.println(" 운행 용도:");
        System.out.println("  1. 출퇴근/가정용");
        System.out.println("  2. 영업용");
        System.out.println("  3. 업무용");
        System.out.print(" 선택: ");
        String purposeStr = sc.nextLine().trim();

        Car.Purpose purpose;
        if ("2".equals(purposeStr))      purpose = Car.Purpose.COMMERCIAL;
        else if ("3".equals(purposeStr)) purpose = Car.Purpose.BUSINESS;
        else                             purpose = Car.Purpose.COMMUTE;
        car.changePurpose(purpose);

        System.out.println("\n 운전자 범위:");
        DriverScope.ScopeType[] scopeOptions = { DriverScope.ScopeType.SELF, DriverScope.ScopeType.FAMILY };
        for (int i = 0; i < scopeOptions.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, scopeOptions[i].getLabel());
        }
        System.out.print(" 선택: ");
        String scopeStr = sc.nextLine().trim();

        DriverScope driverScope = car.getDriverScope();
        if ("2".equals(scopeStr)) {
            // A2: 가족한정 → 가족 Party 생성 및 등록
            System.out.println("\n[가족 정보 입력]");
            System.out.print(" 이름: ");
            String familyName = sc.nextLine().trim();
            System.out.print(" 관계 (예: 동생, 배우자): ");
            String relation = sc.nextLine().trim();
            System.out.print(" 생년월일 (예: 070101): ");
            String birthDate = sc.nextLine().trim();

            int familyAge = calcAgeFromBirthDate(birthDate);
            Party familyParty = new Party();
            familyParty.setPartyId("PARTY-FAM-" + System.currentTimeMillis());
            familyParty.setName(familyName);
            familyParty.setRole(Party.Role.INSURED);

            driverScope.restrictToFamily(familyParty, familyAge);
            System.out.printf(" → 가족 정보 등록: %s (%s, 만 %d세)%n", familyName, relation, familyAge);
        } else {
            driverScope.restrictToSelf();
        }

        // ── Step 8: <<include>> CS-03 예상보험료 산출 ─────────
        long confirmedPremium = new CS03PremiumEstimate().runAsInclude(selectedProduct, stdValue, purpose);
        if (confirmedPremium < 0) {
            returnToMenu();
            return;
        }

        // E2: 자동심사 (영업용 거절, 연령 제한 위반 거절)
        // calcAge가 -1을 반환하면 SSN 파싱 실패이므로 연령 제한은 통과(E1에서 이미 검사)
        int driverAge = Party.calcAge(ssn);
        boolean ageAllowed = (driverAge == -1) || car.isDriverAllowed(driverAge);
        if (!ageAllowed || purpose == Car.Purpose.COMMERCIAL) {
            System.out.println("\n[거절] 가입이 거절되었습니다. 자세한 사항은 고객 센터로 연락주세요.(1588-1000)");
            returnToMenu();
            return;
        }

        // ── Step 9: Contract 생성 및 저장 ────────────────────
        Party holder = new Party();
        holder.setPartyId("PARTY-" + System.currentTimeMillis());
        holder.setName(name);
        holder.setPhone(phone);

        Insured insured = Insured.ofDriver(name, ssn);

        List<SelectedCoverage> selectedCoverages = (selectedProduct.getCoverages() != null)
            ? selectedProduct.getCoverages().stream()
                .map(SelectedCoverage::from)
                .collect(Collectors.toList())
            : List.of();
        List<SelectedRider> selectedRiders = (selectedProduct.getRiders() != null)
            ? selectedProduct.getRiders().stream()
                .map(SelectedRider::from)
                .collect(Collectors.toList())
            : List.of();

        Contract contract = Contract.issue(
            Contract.nextPolicyNo(),
            Contract.nextContractId(),
            selectedProduct,
            holder,
            insured,
            new Money(confirmedPremium, "KRW"),
            car.getCarNumber(),
            selectedCoverages,
            selectedRiders
        );
        // 가족한정인 경우 가족 구성원을 기명피보험자로 등록
        if (driverScope.getScopeType() == DriverScope.ScopeType.FAMILY
                && driverScope.getFamilyMember() != null) {
            contract.setNamedInsured(driverScope.getFamilyMember());
        }
        Contract.save(contract);

        // ── Step 10: 완료 ─────────────────────────────────────
        System.out.println("\n========================================");
        System.out.println(" 보험가입이 완료되었습니다.");
        System.out.println("========================================");
        System.out.printf(" 증권번호    : %s%n", contract.getPolicyNo());
        System.out.printf(" 상품명      : %s%n", selectedProduct.getProductName());
        System.out.printf(" 가입자      : %s%n", name);
        System.out.printf(" 전화번호    : %s%n", phone);
        System.out.printf(" 차량번호    : %s%n", car.getCarNumber());
        System.out.printf(" 운행용도    : %s%n", car.getPurposeLabel());
        System.out.printf(" 운전자범위  : %s%n", driverScope.getScopeLabel());
        System.out.printf(" 보험료      : %,d원/년%n", confirmedPremium);
        returnToMenu();
    }

    /** YYMMDD 6자리 생년월일로 만 나이 계산. 파싱 실패 시 0 반환. */
    private int calcAgeFromBirthDate(String yymmdd) {
        try {
            int yy = Integer.parseInt(yymmdd.substring(0, 2));
            int currentYY = java.time.LocalDate.now().getYear() % 100;
            int birthYear = (yy <= currentYY) ? 2000 + yy : 1900 + yy;
            return java.time.LocalDate.now().getYear() - birthYear;
        } catch (Exception e) {
            return 0;
        }
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
