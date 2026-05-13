package domain;

import java.io.Serializable;
import java.util.List;

public class PremiumCalculation implements Serializable {
    private static final long serialVersionUID = 1L;

    // ── 담보별 순보험료 요율 (차량기준가액 대비) ─────────────
    private static final double RATE_PERSONAL_INJURY_MANDATORY = 0.00989;
    private static final double RATE_PERSONAL_INJURY_OPTIONAL  = 0.01771;
    private static final double RATE_PROPERTY_DAMAGE           = 0.02967;
    private static final double RATE_AUTO_INJURY               = 0.00044;
    private static final double RATE_UNINSURED_VEHICLE         = 0.00093;
    private static final double RATE_OWN_VEHICLE_DAMAGE        = 0.01417;
    private static final double RATE_EMERGENCY_SERVICE         = 0.00146;

    // ── 할인 항목 ─────────────────────────────────────────────
    public enum DiscountItem {
        MILEAGE("마일리지 가입", 0.047),
        NO_ACCIDENT_3Y("3년 무사고 할인", 0.030),
        ABS("ABS 특별요율", 0.020);

        private final String label;
        private final double rate;

        DiscountItem(String label, double rate) {
            this.label = label;
            this.rate  = rate;
        }

        public String getLabel() { return label; }
        public double getRate()  { return rate; }
    }

    private final long personalInjuryMandatory;
    private final long personalInjuryOptional;
    private final long propertyDamage;
    private final long autoInjury;
    private final long uninsuredVehicle;
    private final long ownVehicleDamage;
    private final long emergencyService;
    private final long subtotal;
    private final double discountRate;
    private final long discountAmount;
    private final long finalPremium;

    private PremiumCalculation(long pim, long pio, long pd, long ai, long uv, long ovd, long es,
                                long sub, double discRate) {
        this.personalInjuryMandatory = pim;
        this.personalInjuryOptional  = pio;
        this.propertyDamage          = pd;
        this.autoInjury              = ai;
        this.uninsuredVehicle        = uv;
        this.ownVehicleDamage        = ovd;
        this.emergencyService        = es;
        this.subtotal                = sub;
        this.discountRate            = discRate;
        this.discountAmount          = Math.round(sub * discRate);
        this.finalPremium            = sub - this.discountAmount;
    }

    // ── 정적 팩토리: 보험료 산출 ──────────────────────────────
    public static PremiumCalculation calculate(long stdValue, Car.Purpose purpose,
                                               List<DiscountItem> discounts) {
        double mult     = purposeMultiplier(purpose);
        double discRate = discounts.stream().mapToDouble(DiscountItem::getRate).sum();

        long pim  = Math.round(stdValue * RATE_PERSONAL_INJURY_MANDATORY * mult);
        long pio  = Math.round(stdValue * RATE_PERSONAL_INJURY_OPTIONAL  * mult);
        long pd   = Math.round(stdValue * RATE_PROPERTY_DAMAGE           * mult);
        long ai   = Math.round(stdValue * RATE_AUTO_INJURY               * mult);
        long uv   = Math.round(stdValue * RATE_UNINSURED_VEHICLE         * mult);
        long ovd  = Math.round(stdValue * RATE_OWN_VEHICLE_DAMAGE        * mult);
        long es   = Math.round(stdValue * RATE_EMERGENCY_SERVICE         * mult);
        long sub  = pim + pio + pd + ai + uv + ovd + es;

        return new PremiumCalculation(pim, pio, pd, ai, uv, ovd, es, sub, discRate);
    }

    private static double purposeMultiplier(Car.Purpose purpose) {
        if (purpose == Car.Purpose.BUSINESS)   return 1.1;
        if (purpose == Car.Purpose.COMMERCIAL) return 1.3;
        return 1.0;
    }

    // ── Getters ───────────────────────────────────────────────
    public long getPersonalInjuryMandatory() { return personalInjuryMandatory; }
    public long getPersonalInjuryOptional()  { return personalInjuryOptional; }
    public long getPropertyDamage()          { return propertyDamage; }
    public long getAutoInjury()              { return autoInjury; }
    public long getUninsuredVehicle()        { return uninsuredVehicle; }
    public long getOwnVehicleDamage()        { return ownVehicleDamage; }
    public long getEmergencyService()        { return emergencyService; }
    public long getSubtotal()                { return subtotal; }
    public double getDiscountRate()          { return discountRate; }
    public long getDiscountAmount()          { return discountAmount; }
    public long getFinalPremium()            { return finalPremium; }
}
