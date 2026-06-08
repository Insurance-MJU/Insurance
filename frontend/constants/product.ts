export const PRODUCT_WIZARD_STEPS = ["기본정보", "담보 선택", "특약 선택", "보험료 산출"] as const;
export type ProductWizardStep = typeof PRODUCT_WIZARD_STEPS[number];
