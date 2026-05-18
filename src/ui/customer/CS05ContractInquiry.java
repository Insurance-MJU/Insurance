package ui.customer;

import domain.Contract;
import infra.Context;
import infra.external.IdentityVerificationService;
import java.util.List;
import java.util.Scanner;

public class CS05ContractInquiry {
    private final Scanner sc = Context.getInstance().scanner();

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-05: 보험계약을 조회한다");
        System.out.println("========================================");
        IdentityVerificationService.AuthResult auth =
            new IdentityVerificationService(sc).verify();
        runFlow(auth.name);
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }

    /** CS-04에서 이미 인증이 완료된 경우 — 인증 결과를 직접 전달 */
    public Contract runAsInclude(String holderName) {
        System.out.println("\n========================================");
        System.out.println(" CS-05: 보험계약을 조회한다");
        System.out.println("========================================");
        return runFlow(holderName);
    }

    // ── 계약 조회·선택 본 흐름 ─────────────────────────────────────────────
    private Contract runFlow(String holderName) {
        System.out.println("\n[보험계약 조회]");
        System.out.println(" 조회 기간: 1. 전체  2. 1년  3. 3년");
        System.out.print(" 선택: ");
        String periodChoice = sc.nextLine().trim();
        System.out.println(" 계약 상태: 1. 유지 중  2. 만기  3. 해지");
        System.out.print(" 선택: ");
        String statusChoice = sc.nextLine().trim();
        System.out.println("[조회]");

        List<Contract> contracts = Contract.findByCondition(holderName, periodChoice, statusChoice);

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

        System.out.println("\n[계약 상세 내역 - " + selected.getPolicyNo() + "]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 계약일시   : " + selected.getIssueDateString());
        System.out.printf(" 보험료     : %,d원/년%n", selected.getPremium().getAmount());
        System.out.println(" 담보내용   : " + selected.getCoveragesDescription());
        System.out.println(" 특약목록   : " + selected.getRidersDescription());
        System.out.println(" 차량번호   : " + selected.getCarNumber());
        System.out.println(" 계약상태   : " + selected.getStatusLabel());
        System.out.println("------------------------------------------------------------");

        System.out.print("\n[청구] 버튼 - 이 계약으로 보험금을 청구하시겠습니까? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y")) {
            System.out.println("\n청구 프로세스로 이동합니다.");
            return selected;
        }
        return null;
    }
}
