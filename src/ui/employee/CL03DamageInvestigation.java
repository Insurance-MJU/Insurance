package ui.employee;

import domain.Accident;
import domain.DamageInvestigation;
import infra.Context;
import infra.repository.AccidentRepository;
import infra.repository.InvestigationRepository;

import java.util.Scanner;

public class CL03DamageInvestigation {
    private final Scanner sc = Context.getInstance().scanner();

    private static final int MAX_INJURY_GRADE = 14;

    public void run() {
        System.out.println("\n[CL-03] 손해를 조사한다");
        System.out.println("========================================");

        // Step 1: 사고 접수번호 입력
        System.out.println("\n[ 현장 조사 및 피해 입력 ]");
        System.out.print("사고 접수 번호 (예: ACC-2026-001): ");
        String accNo = sc.nextLine().trim();
        System.out.println("[현장 조사 및 피해 입력]");

        // 레포지토리에서 사고 정보 조회
        Accident accident = AccidentRepository.findById(accNo);

        // Step 3 부터 A1/E1 발생 시 재시작
        while (true) {
            // Step 2: 현장 조사 폼 출력
            System.out.println("\n[ 현장 조사 폼 - " + accNo + " ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  현장출동 소견 / 파손 부위 / 부상 정도 입력");
            System.out.println("------------------------------------------------------------");
            System.out.print("현장 조사 소견: ");
            String opinion = sc.nextLine().trim();
            System.out.print("파손 부위 코드 (예: CAR-D-03): ");
            String damageCode = sc.nextLine().trim();
            System.out.print("부상 급수      (1~14급, 예: 12): ");
            String injuryInput = sc.nextLine().trim();
            System.out.println("[보상 기준 확인]");

            // A1: 파손 부위 코드 필수
            if (damageCode.isEmpty()) {
                System.out.println("\n[경고] >>> 파손 부위 코드 <<< 파손 부위 코드는 현장 조사의 필수 입력란입니다. 코드를 리스트에 추가해 주세요.\n");
                continue;
            }

            // E1: 부상 급수 허용 범위 초과
            int injuryGrade;
            try {
                injuryGrade = Integer.parseInt(injuryInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[오류] >>> 부상 급수 <<< 입력된 급수 값이 허용 범위를 초과하였습니다. (허용: 1~" + MAX_INJURY_GRADE + "급)\n");
                continue;
            }
            if (injuryGrade < 1 || injuryGrade > MAX_INJURY_GRADE) {
                System.out.println("\n[오류] >>> 부상 급수 <<< 입력된 급수 값이 허용 범위를 초과하였습니다. (허용: 1~" + MAX_INJURY_GRADE + "급)\n");
                continue;
            }

            // Step 4: 레포지토리 데이터 기반 보상 한도 범위 출력
            String expectedRepairCost = (accident != null && accident.getExpectedRepairCost() != null)
                ? accident.getExpectedRepairCost() : "미산정";
            String compensationLimit = (accident != null) ? accident.getCoverageLimit() : "1,000만원";

            System.out.println("\n[ 보상 한도 범위 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  예상 수리비       : " + expectedRepairCost);
            System.out.println("  대인 보상 한도     : " + compensationLimit);
            System.out.println("------------------------------------------------------------");

            // Step 5: 과실 비율 입력
            System.out.println("\n[ 과실 비율 입력 ]");
            System.out.print("당사 과실 비율 (%, 예: 80): ");
            String ourFaultInput = sc.nextLine().trim();
            System.out.print("타사 과실 비율 (%, 예: 20): ");
            String otherFaultInput = sc.nextLine().trim();
            System.out.println("[과실 비율 검증]");

            int ourFault;
            int otherFault;
            try {
                ourFault = Integer.parseInt(ourFaultInput);
                otherFault = Integer.parseInt(otherFaultInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[오류] 과실 비율은 숫자로 입력해 주세요.\n");
                continue;
            }

            // Step 6: 과실 비율 검증 결과 출력
            System.out.println("\n[ 과실 비율 검증 결과 ]");
            System.out.println("------------------------------------------------------------");
            if (!DamageInvestigation.validateFaultRatio(ourFault, otherFault)) {
                System.out.println("  합계: " + (ourFault + otherFault) + "% → 합계가 100%가 되어야 합니다.\n");
                continue;
            }
            System.out.println("  합계: 100% → 100% 일치 확인");
            System.out.println("------------------------------------------------------------");

            // Step 7: 면/부책 판정
            System.out.println("\n[ 면/부책 판정 ]");
            System.out.print("면/부책 여부 (면책/부책): ");
            String liability = sc.nextLine().trim();
            System.out.println("[면/부책 판정]");

            // Step 8: 손해 조사 내역 취합 (조사 보고서 초안)
            System.out.println("\n[ 손해 조사 내역 취합 - 조사 보고서 초안 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  접수 번호      : " + accNo);
            System.out.println("  현장 조사 소견 : " + opinion);
            System.out.println("  파손 부위 코드 : " + damageCode);
            System.out.println("  부상 급수      : " + injuryGrade + "급");
            System.out.println("  당사 과실      : " + ourFault + "%");
            System.out.println("  타사 과실      : " + otherFault + "%");
            System.out.println("  면/부책 여부   : " + liability);
            System.out.println("------------------------------------------------------------");

            // Step 9: 최종 조사 의견 입력
            System.out.println("\n[ 조사 완료 및 저장 ]");
            System.out.print("최종 조사 의견 (예: 합의금 산출 진행 요망): ");
            String finalOpinion = sc.nextLine().trim();
            System.out.println("[조사 완료 및 저장]");

            // Step 10: 레포지토리에 조사 결과 저장
            DamageInvestigation inv = DamageInvestigation.create(
                accNo, opinion, damageCode, injuryGrade,
                ourFault, otherFault, liability,
                expectedRepairCost, compensationLimit, finalOpinion
            );
            InvestigationRepository.save(inv);
            AccidentRepository.updateStatus(accNo, "처리중");

            System.out.println("\n┌──────────────────────────────────────────────────────────────┐");
            System.out.println("│  조사 내역이 저장되었습니다. 일시: " + inv.getSavedAt() + "       │");
            System.out.println("└──────────────────────────────────────────────────────────────┘");
            System.out.println("  → CL-02 손해액 산정 Basic Flow 6번으로 이동합니다.");

            break;
        }
    }
}
