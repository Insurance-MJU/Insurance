package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Rider {
    private String description;
    private Double discountRate;
    private List<Exclusion> exclusions;
    private boolean mandatory;
    private String provisionId;
    private String riderCode;
    private String riderId;
    private String riderName;
    private RiderType riderType;

    public enum RiderType { DISCOUNT, EXTRA_COVERAGE, MILEAGE, SAFETY }

    // Setters
    public void setRiderId(String v)       { this.riderId = v; }
    public void setRiderCode(String v)     { this.riderCode = v; }
    public void setRiderName(String v)     { this.riderName = v; }
    public void setDescription(String v)   { this.description = v; }
    public void setDiscountRate(Double v)  { this.discountRate = v; }
    public void setMandatory(boolean v)    { this.mandatory = v; }
    public void setProvisionId(String v)   { this.provisionId = v; }
    public void setRiderType(RiderType v)  { this.riderType = v; }
    public void setExclusions(List<Exclusion> v) { this.exclusions = v; }

    // Getters
    public String getDescription()        { return description; }
    public Double getDiscountRate()       { return discountRate; }
    public List<Exclusion> getExclusions(){ return exclusions; }
    public boolean isMandatory()          { return mandatory; }
    public String getProvisionId()        { return provisionId; }
    public String getRiderCode()          { return riderCode; }
    public String getRiderId()            { return riderId; }
    public String getRiderName()          { return riderName; }
    public RiderType getRiderType()       { return riderType; }

    // ── 영속성 ────────────────────────────────────────────────
    private static final Map<String, Rider> STORE = new LinkedHashMap<>();
    static {
        Rider mileage = new Rider();
        mileage.setRiderId("RIDER-001"); mileage.setRiderCode("RC-MILEAGE");
        mileage.setRiderName("마일리지 특약"); mileage.setRiderType(RiderType.MILEAGE);
        mileage.setDescription(
            "연간환산 운행거리 15,000 km 이하 주행 시, 운행거리 실적에 따라 " +
            "보험료를 환급 받을 수 있는 특약입니다.\n" +
            " - 15,000km 이하: 최대 30% 환급\n" +
            " - 10,000km 이하: 최대 50% 환급\n" +
            " - 5,000km 이하 : 최대 70% 환급"
        );
        mileage.setMandatory(false);
        STORE.put("RC-MILEAGE", mileage);

        Rider tmap = new Rider();
        tmap.setRiderId("RIDER-002"); tmap.setRiderCode("RC-TMAP");
        tmap.setRiderName("티맵안전운전 할인특약"); tmap.setRiderType(RiderType.SAFETY);
        tmap.setDescription(
            "티맵(T map) 앱을 통해 안전운전 점수를 측정하여 점수에 따라 " +
            "보험료를 할인받을 수 있는 특약입니다.\n" +
            " - 90점 이상: 10% 할인\n" +
            " - 80점 이상: 7% 할인\n" +
            " - 70점 이상: 5% 할인"
        );
        tmap.setDiscountRate(0.10); tmap.setMandatory(false);
        STORE.put("RC-TMAP", tmap);

        Rider blackbox = new Rider();
        blackbox.setRiderId("RIDER-003"); blackbox.setRiderCode("RC-BLACKBOX");
        blackbox.setRiderName("블랙박스할인특약"); blackbox.setRiderType(RiderType.DISCOUNT);
        blackbox.setDescription(
            "차량 내 블랙박스 장착 시 보험료를 할인받을 수 있는 특약입니다.\n" +
            " - 전후방 2채널: 5% 할인\n" +
            " - 전후방+측방 4채널: 7% 할인"
        );
        blackbox.setDiscountRate(0.05); blackbox.setMandatory(false);
        STORE.put("RC-BLACKBOX", blackbox);
    }

    public static Rider findByCode(String riderCode) { return STORE.get(riderCode); }
    public static List<Rider> findAll() { return Collections.unmodifiableList(new ArrayList<>(STORE.values())); }
}
