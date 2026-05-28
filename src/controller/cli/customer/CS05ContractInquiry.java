package controller.cli.customer;

import domain.Contract;
import domain.ContractList;
import domain.Subscription;
import domain.SubscriptionList;
import controller.cli.Context;
import infra.external.verification.VerificationService;
import infra.external.verification.dto.OtpSendRequest;
import infra.external.verification.dto.OtpVerifyRequest;
import infra.external.verification.dto.OtpVerifyResponse;
import infra.external.verification.dto.VerifiedIdentity;
import java.util.Scanner;

public class CS05ContractInquiry {
    private final Scanner sc = Context.getInstance().scanner();
    private final SubscriptionList subscriptionList;
    private final ContractList contractList;
    private final VerificationService verificationService;

    public CS05ContractInquiry(SubscriptionList subscriptionList, ContractList contractList,
                               VerificationService verificationService) {
        this.subscriptionList = subscriptionList;
        this.contractList = contractList;
        this.verificationService = verificationService;
    }

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-05: 보험계약을 조회한다");
        System.out.println("========================================");
        // Step 1: 본인 인증
        System.out.println("\n[본인 인증]");
        System.out.print(" 이름: ");         String name  = sc.nextLine().trim();
        System.out.print(" 주민번호: ");      String ssn   = sc.nextLine().trim();
        System.out.print(" 휴대전화번호: ");  String phone = sc.nextLine().trim();
        var sendResp = verificationService.sendOtp(new OtpSendRequest(name, ssn, phone, "1"));
        System.out.print(" 인증번호: ");
        OtpVerifyResponse verifyResp = verificationService.verifyOtp(
            new OtpVerifyRequest(sendResp.sessionId(), sc.nextLine().trim()));
        if (!verifyResp.success()) {
            System.out.println("[오류] 본인 인증 실패: " + verifyResp.errorMessage());
            System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
            sc.nextLine(); System.out.println(); return;
        }
        VerifiedIdentity identity = verificationService.resolveIdentity(verifyResp.verificationToken());
        runFlow(identity.name());
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

        // ── 청약 현황 (승인 전) ──────────────────────────────
        SubscriptionList subscriptions = subscriptionList.findByApplicantName(holderName).excludeApproved();

        if (!subscriptions.isEmpty()) {
            System.out.println("\n[청약 현황]");
            System.out.println("------------------------------------------------------------");
            System.out.printf(" %-20s %-25s %-14s %-12s %-10s%n",
                "청약번호", "상품명", "보험료(예정)", "청약일자", "상태");
            System.out.println("------------------------------------------------------------");
            for (Subscription s : subscriptions.getAll()) {
                System.out.printf(" %-20s %-25s %,10d원  %-12s %-10s%n",
                    s.getSubscriptionNo(), s.getProductName(),
                    s.getPremium().getAmount(),
                    s.getSubscriptionDateDisplay(), s.getStatus().getLabel());
            }
            System.out.println("------------------------------------------------------------");
        }

        // ── 확정 계약 목록 ────────────────────────────────────
        ContractList contracts = contractList.findByCondition(holderName, periodChoice, statusChoice);

        System.out.println("\n[보험 계약 목록]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" %-15s %-35s %-8s%n", "증권번호", "상품명", "상태");
        System.out.println("------------------------------------------------------------");
        if (contracts.isEmpty()) {
            System.out.println("  조회된 계약이 없습니다.");
        } else {
            for (Contract c : contracts.getAll()) {
                System.out.printf(" %-15s %-35s %-8s%n",
                    c.getPolicyNo(), c.getProductName(), c.getStatusLabel());
            }
        }
        System.out.println("------------------------------------------------------------");

        System.out.print("\n조회할 증권번호를 입력하세요 (0: 취소): ");
        String policyNo = sc.nextLine().trim();
        if ("0".equals(policyNo)) return null;

        Contract selected = contracts.findByPolicyNo(policyNo);

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
