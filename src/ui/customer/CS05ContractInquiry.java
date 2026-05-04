package ui.customer;

import domain.Contract;
import infra.Context;
import infra.repository.ContractRepository;
import java.util.List;
import java.util.Scanner;

public class CS05ContractInquiry {
    private final Scanner sc = Context.getInstance().scanner();

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-05: 보험계약을 조회한다");
        System.out.println("========================================");
        runFlow();
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }

    public Contract runAsInclude() {
        System.out.println("\n========================================");
        System.out.println(" CS-05: 보험계약을 조회한다");
        System.out.println("========================================");
        return runFlow();
    }

    private Contract runFlow() {
        // Step 1~2: 본인 인증
        System.out.println("\n[본인 인증]");
        System.out.println(" 1. 공동인증서  2. 간편비밀번호  3. 휴대폰 인증");
        System.out.print(" 인증 수단 선택: ");
        String authMethod = sc.nextLine().trim();

        // A1: 인증 수단 미선택
        if (authMethod.isEmpty() || (!authMethod.equals("1") && !authMethod.equals("2") && !authMethod.equals("3"))) {
            System.out.println("\n[경고] 본인 인증 수단은 필수 사항입니다. 인증 수단을 리스트에 추가해 주세요.");
            System.out.print(" 인증 수단을 다시 선택해 주세요 (1~3): ");
            authMethod = sc.nextLine().trim();
        }

        System.out.print(" 이름: ");
        String holderName = sc.nextLine().trim();
        System.out.print(" 주민등록번호 (예: 020101-3******): ");
        sc.nextLine();
        System.out.print(" 휴대전화번호 (예: 010-1234-5678): ");
        sc.nextLine();
        System.out.print(" 인증번호 (예: 123456): ");
        sc.nextLine();

        System.out.println("\n인증이 완료되었습니다.");

        // Step 3~4: 보험계약 조회 조건
        System.out.println("\n[보험계약 조회]");
        System.out.println(" 조회 기간: 1. 전체  2. 1년  3. 3년");
        System.out.print(" 선택: ");
        String periodChoice = sc.nextLine().trim();
        System.out.println(" 계약 상태: 1. 유지 중  2. 만기  3. 해지");
        System.out.print(" 선택: ");
        String statusChoice = sc.nextLine().trim();
        System.out.println("[조회]");

        // Step 5: 레포지토리에서 계약 목록 조회 및 출력
        List<Contract> contracts = ContractRepository.findByCondition(holderName, periodChoice, statusChoice);

        System.out.println("\n[보험 계약 목록]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" %-15s %-35s %-8s%n", "증권번호", "상품명", "상태");
        System.out.println("------------------------------------------------------------");
        if (contracts.isEmpty()) {
            System.out.println("  조회된 계약이 없습니다.");
        } else {
            for (Contract c : contracts) {
                System.out.printf(" %-15s %-35s %-8s%n",
                    c.getPolicyNo(), c.getProductName(), c.getStatusLabel());
            }
        }
        System.out.println("------------------------------------------------------------");

        // E1: 시스템 내부 오류 (정상 처리)

        // Step 6: 계약 선택
        System.out.print("\n조회할 증권번호를 입력하세요 (0: 취소): ");
        String policyNo = sc.nextLine().trim();
        if ("0".equals(policyNo)) return null;

        Contract selected = contracts.stream()
            .filter(c -> c.getPolicyNo().equals(policyNo))
            .findFirst().orElse(null);

        if (selected == null) {
            System.out.println("[오류] 해당 증권번호를 찾을 수 없습니다.");
            return null;
        }

        // Step 7: 계약 상세 내역
        System.out.println("\n[계약 상세 내역 - " + selected.getPolicyNo() + "]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 계약일시   : " + selected.getIssueDateString());
        System.out.printf(" 보험료     : %,d원/년%n", selected.getPremium().getAmount());
        System.out.println(" 담보내용   : " + selected.getCoveragesDescription());
        System.out.println(" 특약목록   : " + selected.getRidersDescription());
        System.out.println(" 차량번호   : " + selected.getCarNumber());
        System.out.println(" 계약상태   : " + selected.getStatusLabel());
        System.out.println("------------------------------------------------------------");

        // Step 8~9: 청구 버튼
        System.out.print("\n[청구] 버튼 - 이 계약으로 보험금을 청구하시겠습니까? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            System.out.println("\n청구 프로세스로 이동합니다.");
            return selected;
        }
        return null;
    }
}
