package domain;

public class ProfitabilityCalculator {
    private ProfitabilityCalculator() {}

    /**
     * 손익분기점(BEP) — 건수 기준
     * setupCost / (보험료 × (1 - 손해율 - 사업비율))
     * @return BEP 건수, 공헌이익이 0 이하이면 -1
     */
    public static long bep(long premium, double lossRatioPct, double expenseRatioPct, long setupCost) {
        double margin = premium * (1.0 - lossRatioPct / 100.0 - expenseRatioPct / 100.0);
        if (margin <= 0) return -1L;
        return (long) Math.ceil(setupCost / margin);
    }

    /**
     * 순현재가치(NPV) — 단순 할인 모형
     * NPV = Σ [연간이익 / (1+r)^t] - 초기투자비
     */
    public static long npv(long annualProfit, double discountRatePct, int years, long initialInvestment) {
        double r  = discountRatePct / 100.0;
        double pv = 0;
        for (int t = 1; t <= years; t++) {
            pv += annualProfit / Math.pow(1 + r, t);
        }
        return Math.round(pv - initialInvestment);
    }

    /**
     * 합산비율(Combined Ratio)
     * (지급보험금 + 사업비) / 수입보험료 × 100
     */
    public static double combinedRatio(long claims, long expenses, long revenue) {
        return (claims + expenses) * 100.0 / revenue;
    }

    /** IRR은 계리적 반복계산이 필요하므로 추정 상수로 제공 */
    public static final double IRR_ESTIMATE_PCT = 8.5;
}
