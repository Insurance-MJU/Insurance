package ui.employee;

import domain.Accident;
import domain.Claim;
import domain.Contract;
import domain.Deductible;
import domain.SelectedCoverage;
import domain.common.Money;
import infra.Context;

import java.util.Scanner;

public class CL02DamageAssessment {
    private final Scanner sc = Context.getInstance().scanner();

    public void run() {
        System.out.println("\n[CL-02] 손해액을 산정한다");
        System.out.println("========================================");

        // Step 2: 입력란 출력 / Step 3 + E1/A1 발생 시 재시작
        while (true) {
            System.out.println("\n[ 손해액 산정 조회 ]");
            System.out.print("담당자 사번 (예: EMP-1023): ");
            String empNo = sc.nextLine().trim();
            System.out.print("접수 번호   (예: ACC-2026-001): ");
            String accNo = sc.nextLine().trim();
            System.out.println("[산정 대상 조회]");

            // Step 4: 레포지토리에서 사고 초기 접수 내역 + 계약 담보 조회
            Accident accident = Accident.findById(accNo);
            Claim claim = Claim.findByAccidentId(accNo);
            Contract contract = (claim != null)
                ? Contract.findByContractId(claim.getContractId()) : null;

            System.out.println("\n[ 손해액 산정 폼 - 사고 초기 접수 내역 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  접수 번호  : " + accNo);
            System.out.println("  담당자     : " + empNo);
            if (accident != null) {
                System.out.println("  사고 일시  : " + accident.getAccidentDateDisplay());
                System.out.println("  사고 유형  : " + accident.getDescription());
                System.out.println("  사고 장소  : " + accident.getAccidentLocation());
                System.out.println("  피해 차량  : " + accident.getVehicleInfo());
            } else {
                System.out.println("  [해당 접수번호의 사고 정보를 찾을 수 없습니다]");
            }
            System.out.println("------------------------------------------------------------");

            // Step 5 + A1: 피해 내역 조사 진행 여부
            System.out.print("\n조사 진행 여부를 입력하세요 (Y/N): ");
            String proceed = sc.nextLine().trim();
            System.out.println("[피해 내역 조사 실행]");

            // A1: 조사 미완료
            if (!"Y".equalsIgnoreCase(proceed)) {
                System.out.println("\n[경고] 손해 조사가 완료되지 않아 산정을 진행할 수 없습니다. 조사를 완료해 주세요.\n");
                continue;
            }

            // <<include>> CL-03 — 이미 확보한 accNo를 전달하여 이중 입력 방지
            new CL03DamageInvestigation().runAsInclude(accNo);

            // Step 6: 보상 한도액 + 담보별 자기부담금 출력
            String personalLimitDisplay;
            if (accident != null && accident.getPersonalInjuryLimit() != null)
                personalLimitDisplay = accident.getPersonalInjuryLimit().getAmount() / 10_000 + "만원";
            else
                personalLimitDisplay = "1,000만원";
            String coverageLimitDisplay;
            if (accident != null && accident.getCoverageLimit() != null)
                coverageLimitDisplay = accident.getCoverageLimit().getAmount() / 10_000 + "만원";
            else
                coverageLimitDisplay = "2,000만원";

            // 계약 담보에서 자기부담금 기본값 산출
            long contractDeductible = 0L;
            if (contract != null && contract.getSelectedCoverages() != null) {
                for (SelectedCoverage sc : contract.getSelectedCoverages()) {
                    if (sc.getDeductible() != null && sc.getDeductible().getAmount() != null)
                        contractDeductible += sc.getDeductible().getAmount().getAmount();
                }
            }

            System.out.println("\n[ 보상 한도액 및 담보 현황 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  대인 한도 : " + personalLimitDisplay);
            System.out.println("  대물 한도 : " + coverageLimitDisplay);
            if (contract != null && contract.getSelectedCoverages() != null) {
                System.out.println("  가입 담보 :");
                for (SelectedCoverage sc : contract.getSelectedCoverages()) {
                    String dedStr = (sc.getDeductible() != null && sc.getDeductible().getAmount() != null)
                        ? "  자기부담금 " + sc.getDeductible().getAmount().getAmount() / 10_000 + "만원"
                        : "";
                    System.out.println("    - " + sc.getCoverageName() + dedStr);
                }
            }
            System.out.println("------------------------------------------------------------");

            // Step 7 + E1: 합의금·자기부담금 입력
            System.out.println("\n[ 최종 손해액 산출 ]");
            System.out.print("최종 합의금 (만원 단위 숫자, 예: 1500): ");
            String settlementInput = sc.nextLine().trim();
            String dedHint = contractDeductible > 0
                ? "계약 기준 " + contractDeductible / 10_000 + "만원, 예: " + contractDeductible / 10_000
                : "예: 20";
            System.out.print("자기부담금  (만원 단위 숫자, " + dedHint + "): ");
            String deductibleInput = sc.nextLine().trim();
            System.out.println("[최종 손해액 산출]");

            int settlement;
            int deductible;
            try {
                settlement = Integer.parseInt(settlementInput);
                deductible = Integer.parseInt(deductibleInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[오류] >>> 합의금 / 자기부담금 <<< 입력된 합의금 한도 값이 허용 범위를 초과하였습니다.\n");
                continue;
            }

            // E1: 대물 한도 초과
            int limitManwon = (accident != null) ? accident.getCoverageLimitManwon() : 2000;
            if (settlement > limitManwon) {
                System.out.println("\n[오류] >>> 최종 합의금 <<< 입력된 합의금 한도 값이 허용 범위를 초과하였습니다.");
                System.out.println("       대물 한도(" + limitManwon + "만원) 이하로 입력해 주세요.\n");
                continue;
            }

            // Step 8: 최종 결정 손해액 출력
            int finalAmount = settlement - deductible;
            System.out.println("\n[ 최종 결정 손해액 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  최종 합의금     : " + settlement + "만원");
            System.out.println("  자기부담금 공제  : -" + deductible + "만원");
            System.out.println("  실 지급 보상금   : " + finalAmount + "만원");
            System.out.println("------------------------------------------------------------");

            // Step 9: 담당자 결제 의견 입력
            System.out.println("\n[ 산정 내역 승인 ]");
            System.out.print("담당자 결제 의견 (예: 합의 완료 승인): ");
            String opinion = sc.nextLine().trim();
            System.out.println("[산정 내역 승인]");

            // Step 10: 레포지토리에 Claim 지급 정보 저장 및 상태 업데이트
            if (claim != null) {
                Money settlementMoney = new Money(settlement * 10_000L, "KRW");
                Deductible deductibleObj = deductible == 0
                    ? Deductible.none()
                    : Deductible.fixedAmount(new Money(deductible * 10_000L, "KRW"));
                claim.assess(settlementMoney, deductibleObj);
                claim.save();
            }

            System.out.println("\n[ 산정 내역 승인 완료 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  결제 의견  : " + opinion);
            System.out.println("  지급 금액  : " + finalAmount + "만원");
            System.out.println("  상태       : 지급 대기");
            System.out.println("------------------------------------------------------------");

            // Step 11: 보험금 지급 실행 (<<extend>> CL-04)
            System.out.print("\n[보험금 지급 실행] 버튼을 누르려면 Enter를 입력하세요...");
            sc.nextLine();

            new CL04InsurancePayment().run();

            // Step 12: 지급 완료 팝업 (CL-04 완료 후 진입)
            System.out.println("\n┌──────────────────────────────────────────────────┐");
            System.out.println("│  보험금이 지급 완료되었습니다. 상태: 종결        │");
            System.out.println("└──────────────────────────────────────────────────┘");

            break;
        }

        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }

}
