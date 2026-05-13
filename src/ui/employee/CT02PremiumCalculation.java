package ui.employee;

import domain.*;
import infra.Context;
import infra.external.KidiClient;
import infra.repository.ProductRepository;
import domain.ProfitabilityCalculator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CT02PremiumCalculation {
    private final Scanner sc = Context.getInstance().scanner();
    private final ProductRepository productRepo = new ProductRepository();
    private final KidiClient kidiClient = new KidiClient();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    /** 표준 시장 기준 기본 보험료 (담보 구성 기반 계산 전까지 사용하는 가정치) */
    private static final long   STANDARD_BASE_PREMIUM = 1_150_000L;
    /** 보험업법 시행령 제63조에 따른 법정 준비금 적립 비율 */
    private static final double LEGAL_RESERVE_RATIO   = 0.40;

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CT-02: 보험료를 산출한다");
        System.out.println("========================================");

        List<Product> products = productRepo.findAll();
        System.out.println("\n[상품 목록]");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.printf(" %d. %s (%s) [%s]%n",
                    i + 1, p.getProductName(), p.getProductCode(), p.getStatusLabel());
        }
        System.out.print("\n 상품 번호 선택: ");
        int idx;
        try {
            idx = Integer.parseInt(sc.nextLine().trim()) - 1;
            if (idx < 0 || idx >= products.size()) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[오류] 유효하지 않은 선택입니다.");
            returnToMenu(); return;
        }
        Product product = products.get(idx);
        List<String> defaultCoverages = Arrays.asList("대인배상 I", "대인배상 II", "대물배상");

        long[] result = runAsInclude(product.getProductName(), defaultCoverages, product.getSaleEndDate());
        if (result != null) {
            System.out.println("\n========================================");
            System.out.printf(" 최종 확정 보험료   : %,d원%n", result[0]);
            System.out.printf(" 법정 준비금 필요액 : %,d원%n", result[1]);
            System.out.println("========================================");
        }
        returnToMenu();
    }

    /**
     * CT-01에 의해 include.
     * @return long[]{finalPremium, reserve} 또는 null(취소/오류)
     */
    public long[] runAsInclude(String productName, List<String> selectedCoverages, Date saleEndDate) {
        System.out.println("\n========================================");
        System.out.println(" CT-02: 보험료를 산출한다");
        System.out.println("========================================");
        System.out.println("(CT-01에 의해 include)");

        // ── Step 1: 산출 요약 + 수익성 분석 입력 폼 ─────────────
        System.out.println("\n[산출된 보험료 요약]");
        System.out.printf(" 상품명   : %s%n", productName);
        System.out.printf(" 선택 담보: %s%n", String.join(", ", selectedCoverages));

        System.out.println("\n[수익성 분석 입력]");
        System.out.print(" 목표 판매 건수 (예: 5000): ");
        long targetSales;
        try { targetSales = Long.parseLong(sc.nextLine().trim()); }
        catch (NumberFormatException e) { targetSales = 5000; }

        // A1: 분석 대상 기간이 판매기간 초과 검사
        Date analysisEnd = null;
        while (true) {
            System.out.print(" 분석 대상 기간 종료일 (yyyy-MM-dd): ");
            String endStr = sc.nextLine().trim();
            try {
                analysisEnd = SDF.parse(endStr);
                if (saleEndDate != null && analysisEnd.after(saleEndDate)) {
                    System.out.printf("[오류] 분석 대상 기간은 상품 판매 기간(%s 종료)을 초과할 수 없습니다.%n",
                            SDF.format(saleEndDate));
                } else {
                    break;
                }
            } catch (ParseException e) {
                System.out.println("[오류] 날짜 형식이 올바르지 않습니다. (예: 2027-04-30)");
            }
        }

        System.out.print("\n[다음] (Enter): ");
        sc.nextLine();

        // ── Step 3: 보험개발원 참조위험률 출력 ───────────────────
        System.out.println("\n[보험개발원 참조위험률 - 유사 상품 과거 손해율]");
        System.out.println(" 대인 : 78.5%  (참조값)");
        System.out.println(" 대물 : 68.2%  (참조값)");

        // ── Step 4: 예상 손해율 + 사업비율 입력 ─────────────────
        System.out.print("\n 예상 손해율 (%, 예: 72.0): ");
        double lossRatio;
        try { lossRatio = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { lossRatio = 72.0; }

        System.out.print(" 목표 영업비율 (%, 예: 15): ");
        double salesExpense;
        try { salesExpense = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { salesExpense = 15.0; }

        System.out.print(" 목표 관리비율 (%, 예: 10): ");
        double adminExpense;
        try { adminExpense = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { adminExpense = 10.0; }

        // ── Step 5: 보험료 산출 ──────────────────────────────────
        // E1: 시뮬레이션 가용 여부 확인
        if (!kidiClient.isAvailable()) {
            System.out.println("[오류] 현재 수익성 시뮬레이션을 이용할 수 없습니다.");
            return null;
        }

        long finalPremium   = STANDARD_BASE_PREMIUM;
        long netPremium     = Math.round(finalPremium * lossRatio / 100.0);
        long expensePremium = finalPremium - netPremium;
        long reserve        = Math.round(finalPremium * LEGAL_RESERVE_RATIO);

        System.out.println("\n── 담보별 산출 내역 ──────────────────────");
        System.out.printf(" 순보험료    : %,d원  (손해율 %.1f%% 적용)%n", netPremium, lossRatio);
        System.out.printf(" 부가보험료  : %,d원  (사업비율 %.0f%% 적용)%n", expensePremium, salesExpense + adminExpense);
        System.out.println("──────────────────────────────────────────");
        System.out.printf(" 최종 기본 보험료 : %,d원%n", finalPremium);
        System.out.println("\n[담보별 요율 비중]  대인 35%  |  대물 40%  |  기타 25%");

        // ── Step 6: 수익성 시뮬레이션 ───────────────────────────
        System.out.print("\n[수익성 시뮬레이션] (Enter): ");
        sc.nextLine();

        // ── Step 7: 현금 흐름 분석 결과 ─────────────────────────
        long totalRevenue  = finalPremium * targetSales;
        long totalClaims   = Math.round(totalRevenue * lossRatio / 100.0);
        long totalExpenses = Math.round(totalRevenue * (salesExpense + adminExpense) / 100.0);
        long profit        = totalRevenue - totalClaims - totalExpenses;

        // 초기 투자비 = 총 예상 사업비의 10% (상품개발 일회성 비용 추정)
        long setupCost = Math.round(totalExpenses * 0.10);
        double cr  = ProfitabilityCalculator.combinedRatio(totalClaims, totalExpenses, totalRevenue);
        long   bep = ProfitabilityCalculator.bep(finalPremium, lossRatio, salesExpense + adminExpense, setupCost);
        long   npv = ProfitabilityCalculator.npv(profit, 5.0, 2, setupCost);

        System.out.println("\n── 예상 현금 흐름 분석 ───────────────────");
        System.out.printf(" 총 수입보험료   : %,d원%n", totalRevenue);
        System.out.printf(" 예상 지급보험금 : %,d원%n", totalClaims);
        System.out.printf(" 예상 사업비     : %,d원%n", totalExpenses);
        System.out.printf(" 예상 영업이익   : %,d원%n", profit);
        System.out.println("\n── 계리적 수익성 지표 ────────────────────");
        System.out.printf(" NPV (순현재가치) : %,d원%n", npv);
        System.out.printf(" IRR (내부수익률) : %.1f%%  (계리사 추정치)%n", ProfitabilityCalculator.IRR_ESTIMATE_PCT);
        System.out.printf(" 합산비율         : %.1f%%%n", cr);
        System.out.printf(" BEP              : %,d건 판매 시점%n", bep);

        // ── Step 8: 분석 결과 반영 ───────────────────────────────
        System.out.print("\n[분석 결과 반영] (Enter): ");
        sc.nextLine();

        // ── Step 9: 최종 안내 ─────────────────────────────────
        System.out.println("\n┌──────────────────────────────────────────────────────────┐");
        System.out.printf( "│  최종 확정 보험료는 %,d원,                           │%n", finalPremium);
        System.out.printf( "│  법정 준비금 적립 필요액은 %,d원입니다.              │%n", reserve);
        System.out.println("└──────────────────────────────────────────────────────────┘");
        System.out.println("(CT-01의 Basic Flow 10번으로 돌아갑니다.)");

        return new long[]{finalPremium, reserve};
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
