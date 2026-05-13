package domain;

public class Exclusion {
    private String description;
    private String exclusionId;
    private String exclusionName;
    private ExclusionType exclusionType;

    public enum ExclusionType {
        INTENTIONAL,       // 고의사고
        DRUNK_DRIVING,     // 음주운전
        UNLICENSED,        // 무면허 운전
        COMMERCIAL_MISUSE, // 용도 외 사용 (가정용 → 영업용 무단 전환)
        WAR_RIOT,          // 전쟁·폭동
        NUCLEAR,           // 핵 사고
        RACING             // 경기·경연 중 사고
    }

    public String getDescription() { return description; }
    public String getExclusionId() { return exclusionId; }
    public String getExclusionName() { return exclusionName; }
    public ExclusionType getExclusionType() { return exclusionType; }
}
