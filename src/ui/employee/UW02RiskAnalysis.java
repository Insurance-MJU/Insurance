package ui.employee;

import domain.CreditInfo;
import domain.RiskAnalysisReport;
import domain.Subscription;
import domain.common.Money;
import infra.Context;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class UW02RiskAnalysis {
    private final Scanner sc = Context.getInstance().scanner();
    private static final NumberFormat NF = NumberFormat.getInstance(Locale.KOREA);

    public void run() {
        System.out.println("\n[UW-02] 위험성을 분석한다");
        System.out.println("========================================");
        System.out.print(" 청약자 이름: ");
        String name = sc.nextLine().trim();
        System.out.print(" 주민번호 (예: 020101-3******): ");
        String ssn = sc.nextLine().trim();
        System.out.print(" 차량번호: ");
        String carNo = sc.nextLine().trim();

        Subscription tempSub = new Subscription();
        tempSub.setSubscriptionNo("STANDALONE");
        tempSub.setApplicantName(name);
        tempSub.setSsn(ssn);
        tempSub.setCarNumber(carNo);
        tempSub.setBasePremium(new Money(0L, "KRW"));
        runAsInclude(tempSub);

        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }

    public void runAsInclude(Subscription sub) {
        // Step 1: 신용정보원 조회 화면
        System.out.println("\n[ 신용정보원 조회 ]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 조회 대상  : " + sub.getApplicantName()
            + " / " + sub.getSsn() + " / " + sub.getCarNumber());
        System.out.println("------------------------------------------------------------");

        // Step 2: 조회 항목 선택
        System.out.println("\n 조회 항목:");
        System.out.println("  [V] 최근 3년 사고이력");
        System.out.println("  [V] 운전경력");
        System.out.println("  [V] 신용등급");
        System.out.println("  [V] 보험사기 의심이력");
        System.out.print("\n위 항목으로 조회하시겠습니까? (Y/N): ");
        String proceed = sc.nextLine().trim();

        // E1: 신용정보원 시스템 연결 실패
        if (!"Y".equalsIgnoreCase(proceed)) {
            System.out.println("\n[경고] 신용정보원 시스템 연결에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
            return;
        }
        System.out.println("[조회]");

        // Step 3: 신용정보원 조회 결과
        CreditInfo creditInfo = CreditInfo.findByApplicant(sub.getSsn(), sub.getCarNumber());

        // A1: 신규 가입자 데이터 없음
        if (creditInfo == null) {
            System.out.println("\n[안내] 신용정보원에 해당 청약자의 조회 이력이 없습니다. 기본 위험등급(3등급)이 적용됩니다.");
            RiskAnalysisReport defaultReport =
                RiskAnalysisReport.defaultForNewApplicant(sub.getSubscriptionNo(), sub.getBasePremium());
            RiskAnalysisReport.save(defaultReport);
            printSummary(defaultReport);
            confirmResult();
            return;
        }

        System.out.println("\n[ 신용정보원 조회 결과 ]");
        System.out.println("------------------------------------------------------------");
        if (!creditInfo.hasAccidentHistory()) {
            System.out.println(" 최근 3년 사고이력 : 없음");
        } else {
            for (CreditInfo.AccidentRecord acc : creditInfo.getAccidentHistory()) {
                System.out.printf(" 최근 3년 사고이력 : %s - %s (%s원)%n",
                    acc.getDateDisplay(), acc.getDescription(),
                    NF.format(acc.getAmount().getAmount()));
            }
        }
        System.out.println(" 운전경력          : 보험 가입 경력 " + creditInfo.getDrivingExperienceYears() + "년");
        System.out.println(" 신용등급          : " + creditInfo.getCreditGrade());
        System.out.println(" 보험사기 이력     : " + creditInfo.getFraudHistory());
        System.out.println("------------------------------------------------------------");

        // Step 4: 위험등급 산출
        System.out.print("\n[위험등급 산출] 버튼을 누르려면 Enter를 입력하세요...");
        sc.nextLine();
        System.out.println("[위험등급 산출]");

        // 도메인이 직접 분석 수행
        RiskAnalysisReport report =
            RiskAnalysisReport.analyze(sub.getSubscriptionNo(), sub.getBasePremium(), creditInfo);
        RiskAnalysisReport.save(report);

        // Step 5: 위험 분석 결과 요약
        printSummary(report);

        // Step 6: 상세보기
        System.out.print("\n[상세보기] 버튼을 누르려면 Enter를 입력하세요...");
        sc.nextLine();

        // Step 7: 위험등급 산출 상세 내역
        System.out.println("\n[ 위험등급 산출 상세 내역 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" 사고 건수    : %d건  →  -%.1f점%n",
            creditInfo.getAccidentCount(), report.getAccidentScore());
        System.out.printf(" 운전 경력    : %d년  →  -%.1f점%n",
            creditInfo.getDrivingExperienceYears(), report.getDrivingExpScore());
        System.out.printf(" 신용등급     : %s → -%.1f점%n",
            creditInfo.getCreditGrade(), report.getCreditGradeScore());
        System.out.printf(" 총점         : %.1f점%n", report.getRiskScore());
        System.out.println("------------------------------------------------------------");
        System.out.println(" [등급 판정 기준]");
        System.out.println("  1등급(안전) : 0.0~0.5점   2등급(보통) : 0.6~1.0점");
        System.out.println("  3등급(주의) : 1.1~2.0점   4등급(위험) : 2.1~3.0점");
        System.out.println("  5등급(고위험): 3.1점 이상");
        System.out.println("------------------------------------------------------------");

        confirmResult();
    }

    private void printSummary(RiskAnalysisReport report) {
        System.out.println("\n[ 위험 분석 결과 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf(" 최종 위험 점수  : %.1f점%n", report.getRiskScore());
        System.out.println(" 위험 등급       : " + report.getRiskGradeLabel());
        System.out.printf(" 보험료 할증율   : +%.0f%%%n", report.getSurchargeRate() * 100);
        System.out.println("------------------------------------------------------------");
    }

    private void confirmResult() {
        // Step 8: 분석 결과 확정
        System.out.print("\n[분석 결과 확정] 버튼을 누르려면 Enter를 입력하세요...");
        sc.nextLine();
        System.out.println("[분석 결과 확정]");

        // Step 9: 완료
        System.out.println("\n위험 분석 데이터가 성공적으로 반영되었습니다.");
        System.out.println("  → UW-01: 계약인수를 심사한다. Basic Flow 6번으로 이동합니다.");
    }
}
