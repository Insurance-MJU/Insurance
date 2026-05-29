// ─── 차량 정보 ────────────────────────────────────────────────────────

export interface VehicleInfo {
  carNumber: string;
  manufacturer: string;
  modelName: string;
  modelType: string;
  modelYear: number;
  engineCC: number;
  fuelType: string;
  standardValue: number;
  hasABS: boolean;
  airbagCount: number;
  hasBlackbox: boolean;
}

// ─── 보험료 계산 결과 ─────────────────────────────────────────────────

export interface CoveragePremiumItem {
  name: string;
  limitDesc: string;
  premium: number;
}

export interface DiscountItem {
  name: string;
  amount: number;
}

export interface QuoteResult {
  coverages: CoveragePremiumItem[];
  appliedDiscounts: DiscountItem[];
  totalBeforeDiscount: number;
  totalDiscount: number;
  totalPremium: number;
  riskGrade: string;
  discountSurchargeGrade: string;
}

// ─── 당사자 정보 ──────────────────────────────────────────────────────

export interface PartyInput {
  name: string;
  ssn: string;
  phone: string;
}

// ─── 가족 구성원 (Step07) ─────────────────────────────────────────────

export interface FamilyMember {
  id: string;
  birthYear: number;
  gender: 'M' | 'F';
  relation: 'SPOUSE' | 'PARENT' | 'CHILD' | 'OTHER';
}

// ─── 운전자 범위 ──────────────────────────────────────────────────────

export type DriverScope = 'NAMED_ONLY' | 'COUPLE' | 'FAMILY' | 'ALL';
export type VerificationMethod = 'PHONE' | 'KAKAO' | 'TOSS' | 'PASS';
export type VehiclePurpose = 'COMMUTE' | 'BUSINESS' | 'DELIVERY';

// ─── 선택된 할인 특약 ─────────────────────────────────────────────────

export interface SelectedAdjustment {
  itemName: string;
  adjType: string;
  rate: number;
}

// ─── Enrollment 전체 상태 ─────────────────────────────────────────────

export interface EnrollmentState {
  step: number;
  productId: number;
  productIdStr: string;

  // Step 1 – 소유자
  ownerName: string;
  ownerSsnFront: string;
  ownerSsnBack: string;
  ownerPhone: string;

  // Step 2 – 동의
  consentRequired: Record<string, boolean>;
  consentOptional: boolean;

  // Step 3 – 본인인증
  verificationMethod: VerificationMethod;
  verificationSessionId: string | null;
  verificationToken: string | null;

  // Step 4-5 – 차량
  carNumber: string;
  vehicleInfo: VehicleInfo | null;
  overrideModelYear: number | null;
  overrideValue: number | null;
  hasBlackbox: boolean;
  hasAdvancedSafety: boolean;

  // Step 6 – 마일리지/용도
  mileageDiscount: boolean;
  vehiclePurpose: VehiclePurpose;

  // Step 7 – 운전자 범위
  driverScope: DriverScope;
  driverMinAge: number;
  familyMembers: FamilyMember[];

  // Step 8 – 할인특약
  selectedAdjustments: SelectedAdjustment[];

  // Step 9 – 보험료
  quote: QuoteResult | null;

  // Step 10 – 당사자
  insured: PartyInput;
  contractor: PartyInput;
  isSamePerson: boolean;

  // Step 11 – 청약
  proposalId: string | null;
  policyNo: string | null;
}
