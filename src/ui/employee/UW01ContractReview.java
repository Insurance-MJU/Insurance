package ui.employee;

import infra.Context;
import java.util.Scanner;

public class UW01ContractReview {
    private final Scanner sc = Context.getInstance().scanner();

    public void run() {
        System.out.println("\n[UW-01] 계약인수를 심사한다");
        System.out.println("========================================");

        // Step 1~2: 심사 대기 목록 출력
        System.out.println("\n[ 심사 대기 중인 청약 목록 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" %-20s %-10s %-25s %-12s %-12s %-10s%n",
                "청약번호", "청약자명", "상품명", "보험료", "청약일자", "상태");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" %-20s %-10s %-25s %-12s %-12s %-10s%n",
                "20260401-0001", "박수현", "MZ세대 다이렉트 차보험", "2,907,200원", "2026-04-01", "심사대기중");
        System.out.println("------------------------------------------------------------");

        // Step 3: 청약 건 선택
        System.out.print("\n심사할 청약번호를 입력하세요 (0: 취소): ");
        String appNo = sc.nextLine().trim();
        if ("0".equals(appNo)) {
            returnToMenu();
            return;
        }

        // Step 4: 청약 상세 정보
        System.out.println("\n[ 청약 상세 정보 - " + appNo + " ]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 성명        : 박수현");
        System.out.println(" 주민번호    : 020101-3******");
        System.out.println(" 주소        : 서울시 강남구");
        System.out.println(" 차량번호    : 64마0866");
        System.out.println(" 차대번호    : KMHCT41DBLU123");
        System.out.println(" 가입담보    : 대인I/II, 대물 5억, 자상 1억, 무보험 2억, 자차 가입");
        System.out.println("------------------------------------------------------------");

        // Step 5: 위험성 분석 → include UW-02
        System.out.print("\n[위험성 분석] 버튼을 누르려면 Enter를 입력하세요...");
        sc.nextLine();
        new UW02RiskAnalysis().runAsInclude("박수현", "020101-3******", "64마0866");

        // Step 6: 위험 분석 완료 후 심사 의견 입력
        System.out.println("\n[ 위험 분석 보고서 ]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 위험 등급      : 3등급 [조금 위험]");
        System.out.println(" 사고 점수      : 1.5점");
        System.out.println(" 법규위반 점수  : 0점");
        System.out.println(" 심사 가이드    : 사고이력 존재하나 할증 범위 내");
        System.out.println("------------------------------------------------------------");

        // Step 7: 최종 심사 의견 입력
        System.out.print("\n최종 심사 의견을 입력하세요: ");
        String opinion = sc.nextLine().trim();
        System.out.println("[저장]");

        // Step 8: 최종 결과 화면
        System.out.println("\n[ 최종 심사 결과 ]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 기본 보험료  : 2,794,010원");
        System.out.println(" 위험 할증    : +5%  (139,700원)");
        System.out.println(" 합계 보험료  : 2,933,710원");
        System.out.println(" 심사역       : 김민욱");
        System.out.println(" 심사 일시    : 2026-04-25 22:15");
        System.out.println(" 심사 의견    : " + opinion);
        System.out.println("------------------------------------------------------------");
        System.out.println(" 1. 인수 승인");
        System.out.println(" 2. 인수 거절");
        System.out.println(" 3. 서류보완 요청");
        System.out.print(" 선택: ");
        String decision = sc.nextLine().trim();

        // Step 9: 인수 결정
        switch (decision) {
            case "1":
                // Step 10: 인수 승인
                System.out.println("\n[인수 승인]");
                // E1: 시스템 내부 오류 (정상 처리)
                System.out.println("계약번호(CN-2026-9981)의 인수가 승인되었습니다.");
                break;

            case "2":
                // A1: 인수 거절
                System.out.print("\n인수 거절 사유를 입력하세요: ");
                String rejectReason = sc.nextLine().trim();
                System.out.println("[인수 거절]");
                System.out.println("인수 거절과 함께 거절 사유를 전송했습니다.");
                System.out.println(" 거절 사유: " + rejectReason);
                break;

            case "3":
                // A2: 서류보완 요청
                System.out.print("\n보완 요청할 서류를 입력하세요 (예: 운전경력증명서): ");
                String docRequest = sc.nextLine().trim();
                System.out.println("[서류보완 요청]");
                System.out.println("서류 보완을 요청했습니다.");
                System.out.println(" 요청 서류: " + docRequest);
                break;

            default:
                System.out.println("[오류] 올바른 번호를 선택해주세요.");
        }

        returnToMenu();
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
