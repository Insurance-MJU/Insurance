package ui.employee;

import domain.Claim;
import infra.Context;
import infra.repository.AccidentRepository;
import infra.repository.ClaimRepository;

import java.util.List;
import java.util.Scanner;

public class CL04InsurancePayment {
    private final Scanner sc = Context.getInstance().scanner();


    public void run() {
        System.out.println("\n[CL-04] 보험금을 지급한다");
        System.out.println("========================================");

        // Step 1: 레포지토리에서 지급 대기 목록 조회
        List<Claim> waitingList = ClaimRepository.findAwaitingPayment();

        System.out.println("\n[ 지급 대기 중인 접수 목록 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s %-12s %-10s%n", "접수 번호", "고객명", "지급액");
        System.out.println("------------------------------------------------------------");
        if (waitingList.isEmpty()) {
            System.out.println("  지급 대기 중인 건이 없습니다.");
        } else {
            for (Claim c : waitingList) {
                System.out.printf("%-15s %-12s %-10s%n",
                    c.getAccidentId(), c.getClaimantName(), c.getCompensationAmount() + "만원");
            }
        }
        System.out.println("------------------------------------------------------------");

        // Step 2: 접수번호 입력
        System.out.print("\n접수 번호 (예: ACC-2026-001): ");
        String accNo = sc.nextLine().trim();
        System.out.println("[지급 정보 확인]");

        // 레포지토리에서 청구 정보 조회
        Claim claim = ClaimRepository.findByAccidentId(accNo);

        // Steps 3~11: E1 발생 시 Step 3부터 재시작
        while (true) {
            // Step 3: 지급 대상 고객 정보 출력
            System.out.println("\n[ 지급 대상 고객 정보 - " + accNo + " ]");
            System.out.println("------------------------------------------------------------");
            if (claim != null) {
                System.out.println("  성명   : " + claim.getClaimantName());
                System.out.println("  지급액 : " + claim.getCompensationAmount() + "만원");
            } else {
                System.out.println("  [해당 접수번호의 청구 정보를 찾을 수 없습니다]");
            }
            System.out.println("------------------------------------------------------------");

            // Step 4 + A1: 수령 은행명·계좌번호 필수
            String bank;
            String accountNo;
            while (true) {
                System.out.println("\n[ 계좌 유효성 검증 ]");
                System.out.print("수령 은행명 (예: 한국은행): ");
                bank = sc.nextLine().trim();
                System.out.print("계좌 번호   (예: 123-456-7890): ");
                accountNo = sc.nextLine().trim();
                System.out.println("[계좌 유효성 검증]");

                // A1: 필수값 누락
                if (bank.isEmpty() || accountNo.isEmpty()) {
                    System.out.println("\n[경고] 수령 은행명과 계좌번호는 필수 사항입니다. 정보를 리스트에 추가해 주세요.\n");
                    continue;
                }
                break;
            }

            // E1: 계좌번호 자릿수 초과
            if (!Claim.isValidAccountNumber(accountNo)) {
                System.out.println("\n[오류] >>> 계좌 번호 <<< 입력된 계좌번호 자릿수 값이 허용 범위를 초과하였습니다.\n");
                continue;
            }

            // Step 5: 예금주 실명 일치 결과 출력
            System.out.println("\n[ 예금주 실명 일치 여부 결과 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  은행   : " + bank);
            System.out.println("  계좌   : " + accountNo);
            System.out.println("  결과   : 검증 완료");
            System.out.println("------------------------------------------------------------");

            // Step 6: 결재용 사정의견서 업로드
            System.out.println("\n[ 결재 상신 ]");
            System.out.print("결재용 사정의견서 파일명 (예: 의견서_ACC001.pdf): ");
            String fileName = sc.nextLine().trim();
            System.out.println("[결재 상신]");

            // Step 7: 이체 품의서 요약 출력
            String payAmount = (claim != null) ? claim.getCompensationAmount() + "만원" : "0만원";

            System.out.println("\n[ 이체 품의서 요약 내역 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  지급액     : " + payAmount);
            System.out.println("  수령 은행  : " + bank);
            System.out.println("  계좌 번호  : " + accountNo);
            System.out.println("  첨부 파일  : " + fileName);
            System.out.println("------------------------------------------------------------");

            // Step 8: 결제 인증 비밀번호 입력
            System.out.println("\n[ 최종 이체 승인 ]");
            System.out.print("결제 인증 비밀번호: ");
            sc.nextLine();
            System.out.println("[최종 이체 승인]");

            // Step 9: 이체 처리 경과 출력 (Progress Bar)
            System.out.println("\n[ 은행 API 연동 - 실시간 계좌 이체 처리 ]");
            System.out.print("  처리 중  [");
            for (int i = 0; i < 20; i++) System.out.print("█");
            System.out.println("]  완료");

            // Step 10: 이체 완료 확인
            System.out.print("\n이체 완료를 확인하고 [사고 처리 종결] 버튼을 누르려면 Y를 입력하세요: ");
            sc.nextLine();
            System.out.println("[사고 처리 종결]");

            // Step 11: 레포지토리에 지급 완료 상태 저장
            if (claim != null) {
                claim.completePayment(bank, accountNo);
                ClaimRepository.save(claim);
                AccidentRepository.updateStatus(accNo, "완료");
            }

            System.out.println("\n[ 사고 건 지급 종결 내역 ]");
            System.out.println("------------------------------------------------------------");
            System.out.println("  접수 번호  : " + accNo);
            System.out.println("  상태       : 지급 종결");
            System.out.println("  알림톡     : 발송 완료");
            System.out.println("------------------------------------------------------------");
            System.out.println("  → CL-02 손해액 산정 Basic Flow 12번으로 이동합니다.");

            break;
        }
    }
}
