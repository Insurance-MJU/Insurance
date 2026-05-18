package ui.employee;

import domain.Contract;
import domain.Party;
import domain.RiskAnalysisReport;
import domain.Subscription;
import domain.common.Money;
import infra.Context;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class UW01ContractReview {
    private final Scanner sc = Context.getInstance().scanner();
    private static final NumberFormat NF = NumberFormat.getInstance(Locale.KOREA);

    public void run() {
        System.out.println("\n[UW-01] 계약인수를 심사한다");
        System.out.println("========================================");

        // Step 2: 심사 대기 목록 출력
        List<Subscription> pendingList = Subscription.findPendingReview();
        System.out.println("\n[ 심사 대기 중인 청약 목록 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" %-20s %-10s %-25s %-12s %-12s %-10s%n",
            "청약번호", "청약자명", "상품명", "보험료", "청약일자", "상태");
        System.out.println("------------------------------------------------------------");
        for (Subscription s : pendingList) {
            System.out.printf(" %-20s %-10s %-25s %-12s %-12s %-10s%n",
                s.getSubscriptionNo(), s.getApplicantName(), s.getProductName(),
                NF.format(s.getPremium().getAmount()) + "원",
                s.getSubscriptionDateDisplay(), s.getStatus().getLabel());
        }
        System.out.println("------------------------------------------------------------");

        // Step 3: 청약 건 선택
        System.out.print("\n심사할 청약번호를 입력하세요 (0: 취소): ");
        String appNo = sc.nextLine().trim();
        if ("0".equals(appNo)) {
            returnToMenu();
            return;
        }

        Subscription sub = Subscription.findByNo(appNo);
        if (sub == null) {
            System.out.println("[오류] 해당 청약번호를 찾을 수 없습니다.");
            returnToMenu();
            return;
        }

        // Step 4: 청약 상세 정보
        System.out.println("\n[ 청약 상세 정보 - " + sub.getSubscriptionNo() + " ]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 성명        : " + sub.getApplicantName());
        System.out.println(" 주민번호    : " + sub.getSsn());
        System.out.println(" 주소        : " + sub.getAddress());
        System.out.println(" 차량번호    : " + sub.getCarNumber());
        System.out.println(" 차대번호    : " + sub.getChassisNumber());
        System.out.println(" 가입담보    : " + sub.getCoveragesDescription());
        System.out.println("------------------------------------------------------------");

        // Step 5: 위험성 분석 → include UW-02
        System.out.print("\n[위험성 분석] 버튼을 누르려면 Enter를 입력하세요...");
        sc.nextLine();
        new UW02RiskAnalysis().runAsInclude(sub);

        // Step 6: 위험 분석 보고서 출력
        RiskAnalysisReport report = RiskAnalysisReport.findBySubscriptionNo(sub.getSubscriptionNo());
        if (report == null) {
            System.out.println("[오류] 위험 분석 결과를 불러올 수 없습니다.");
            returnToMenu();
            return;
        }
        System.out.println("\n[ 위험 분석 보고서 ]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 위험 등급      : " + report.getRiskGradeLabel());
        System.out.printf(" 사고 점수      : %.1f점%n", report.getAccidentScore());
        System.out.printf(" 법규위반 점수  : %.1f점%n", report.getTrafficViolationScore());
        System.out.println(" 심사 가이드    : " + report.getReviewGuide());
        System.out.println("------------------------------------------------------------");

        // Step 7: 최종 심사 의견 입력
        System.out.println("\n[ 청약자 참고 정보 ]");
        System.out.println(" 직업: " + sub.getOccupation() + "  /  연령: 만 " + sub.getAge() + "세");
        System.out.print("\n최종 심사 의견을 입력하세요: ");
        String opinion = sc.nextLine().trim();

        report.confirm(Context.getInstance().getCurrentUser().getName(), opinion);
        RiskAnalysisReport.save(report);
        System.out.println("[저장]");

        // Step 8: 최종 결과 화면
        System.out.println("\n[ 최종 심사 결과 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" 기본 보험료  : %s원%n", NF.format(report.getBasePremium().getAmount()));
        System.out.printf(" 위험 할증    : +%.0f%%  (%s원)%n",
            report.getSurchargeRate() * 100,
            NF.format(report.getSurchargeAmount().getAmount()));
        System.out.printf(" 합계 보험료  : %s원%n", NF.format(report.getTotalPremium().getAmount()));
        System.out.println(" 심사역       : " + report.getReviewerName());
        System.out.println(" 심사 일시    : " + report.getReviewDateDisplay());
        System.out.println(" 심사 의견    : " + report.getReviewOpinion());
        System.out.println("------------------------------------------------------------");
        System.out.println(" 1. 인수 승인");
        System.out.println(" 2. 인수 거절");
        System.out.println(" 3. 서류보완 요청");
        System.out.print(" 선택: ");
        String decision = sc.nextLine().trim();

        // Step 9: 인수 결정 — 도메인 메서드로 상태 전이
        switch (decision) {
            case "1":
                // Step 10: 인수 승인 → 계약 발행
                sub.approve();
                Subscription.save(sub);

                Party holder = new Party();
                holder.setPartyId("PARTY-" + sub.getSubscriptionNo());
                holder.setName(sub.getApplicantName());

                Money finalPremium = report.getTotalPremium();
                String policyNo = Contract.nextPolicyNo();
                String contractId = Contract.nextContractId();

                Contract contract = Contract.issue(
                    policyNo, contractId, sub.getProductName(),
                    holder, finalPremium, sub.getCarNumber(),
                    sub.getCoveragesDescription(), "", ""
                );
                contract.setSubscriptionNo(sub.getSubscriptionNo());
                contract.save();

                System.out.println("\n[인수 승인]");
                System.out.println("계약번호(" + policyNo + ")의 인수가 승인되었습니다.");
                break;

            case "2":
                // A1: 인수 거절
                System.out.print("\n인수 거절 사유를 입력하세요: ");
                String rejectReason = sc.nextLine().trim();
                sub.reject(rejectReason);
                Subscription.save(sub);
                System.out.println("[인수 거절]");
                System.out.println("인수 거절과 함께 거절 사유를 전송했습니다.");
                System.out.println(" 거절 사유: " + sub.getRejectReason());
                break;

            case "3":
                // A2: 서류보완 요청
                System.out.print("\n보완 요청할 서류를 입력하세요 (예: 운전경력증명서): ");
                String docs = sc.nextLine().trim();
                sub.requestSupplement(docs);
                Subscription.save(sub);
                System.out.println("[서류보완 요청]");
                System.out.println("서류 보완을 요청했습니다.");
                System.out.println(" 요청 서류: " + sub.getSupplementDocuments());
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
