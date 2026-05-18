package domain;

public enum CoverageType {
    PERSONAL_INJURY_MANDATORY,  // 대인배상 I (의무)
    PERSONAL_INJURY_OPTIONAL,   // 대인배상 II
    PROPERTY_DAMAGE,            // 대물배상
    AUTO_INJURY,                // 자동차상해
    OWN_VEHICLE_DAMAGE,         // 자기차량손해
    UNINSURED_VEHICLE           // 무보험차상해
}
