export const COVERAGE_TYPES = [
    { value: "BODILY_INJURY_1",   label: "대인배상I" },
    { value: "BODILY_INJURY_2",   label: "대인배상II" },
    { value: "PROPERTY_DAMAGE",   label: "대물배상" },
    { value: "AUTO_BODILY_INJURY", label: "자동차상해" },
    { value: "SELF_VEHICLE_DAMAGE", label: "자기차량손해" },
    { value: "UNINSURED_MOTORIST", label: "무보험차상해" },
];

export const LIMIT_TYPES = [
    { value: "UNLIMITED",     label: "무한" },
    { value: "FIXED",         label: "고정 금액" },
    { value: "VEHICLE_VALUE", label: "차량가액" },
];

export const DEDUCTIBLE_TYPES = [
    { value: "NONE",         label: "없음" },
    { value: "FIXED_AMOUNT", label: "정액" },
    { value: "RATE",         label: "비율" },
];

export const LIMIT_UNITS = [
    { value: "PER_PERSON",   label: "인당 한도" },
    { value: "PER_ACCIDENT", label: "사고당 한도" },
    { value: "COMPLEX",      label: "복합" },
];

export const COMPENSATION_TYPES = [
    { value: "ACTUAL_LOSS",  label: "실손" },
    { value: "FIXED_AMOUNT", label: "정액" },
];

export const LIMIT_DETAIL_TYPES = [
    { value: "TOTAL",       label: "총한도" },
    { value: "DEATH",       label: "사망" },
    { value: "INJURY",      label: "부상" },
    { value: "DISABILITY",  label: "후유장해" },
];

export const RATE_TYPES = [
    { value: "VEHICLE_TYPE",   label: "차종별 위험률" },
    { value: "GENDER_AGE",     label: "성·연령별 위험률" },
    { value: "COVERAGE",       label: "보험종목·담보별 위험률" },
    { value: "DRIVER_LIMIT",   label: "운전자한정별 위험률" },
    { value: "DRIVER_AGE_LIMIT", label: "운전자연령한정특약별 위험률" },
    { value: "INTEREST",       label: "예정이율" },
    { value: "EXPENSE",        label: "사업비율" },
];

export const STAT_TYPES = [
    { value: "VEHICLE_TYPE",   label: "차종별",            dim1: "차종",        dim2: null },
    { value: "GENDER_AGE",     label: "성·연령별",          dim1: "성별",        dim2: "연령대" },
    { value: "COVERAGE",       label: "보험종목·담보별",     dim1: "보험종목",     dim2: "담보" },
    { value: "DRIVER_AGE_LIMIT", label: "운전자연령한정특약", dim1: "운전자한정종류", dim2: "연령한정특약" },
    { value: "DRIVER_LIMIT",   label: "운전자한정",         dim1: "보험종목",     dim2: "담보" },
];

export const EXCLUSION_TYPES = [
    { value: "INTENTIONAL",  label: "고의사고" },
    { value: "UNLICENSED",   label: "무면허 운전" },
    { value: "DRUNK_DRIVING", label: "음주운전" },
    { value: "WAR",          label: "전쟁/폭동" },
    { value: "NUCLEAR",      label: "핵연료 등 방사성 물질" },
    { value: "RACING",       label: "자동차 경기/시험" },
    { value: "OTHER",        label: "기타" },
];

export const RIDER_TYPES = [
    { value: "DISCOUNT", label: "할인 특약" },
    { value: "ADD_ON",   label: "보장 추가 특약" },
];

export const LEVEL_TYPES = [
    { value: "PART",      label: "편" },
    { value: "CHAPTER",   label: "장" },
    { value: "SECTION",   label: "절" },
    { value: "ARTICLE",   label: "조" },
    { value: "PARAGRAPH", label: "항" },
    { value: "ITEM",      label: "호" },
    { value: "SUB_ITEM",  label: "목" },
];
