import { create } from 'zustand';
import type {
  EnrollmentState,
  VehicleInfo,
  QuoteResult,
  PartyInput,
  FamilyMember,
  SelectedAdjustment,
  DriverScope,
  VerificationMethod,
  VehiclePurpose,
} from '@/types/enrollment';

// ─── Store Actions ────────────────────────────────────────────────────

interface EnrollmentActions {
  setStep: (step: number) => void;
  nextStep: () => void;
  prevStep: () => void;
  setProductId: (id: number) => void;
  setProductIdStr: (id: string) => void;

  // Step 1
  setOwner: (data: Partial<Pick<EnrollmentState, 'ownerName' | 'ownerSsnFront' | 'ownerSsnBack' | 'ownerPhone'>>) => void;

  // Step 2
  setConsentRequired: (key: string, value: boolean) => void;
  setConsentAll: (value: boolean) => void;
  setConsentOptional: (value: boolean) => void;

  // Step 3
  setVerificationMethod: (method: VerificationMethod) => void;
  setVerificationSession: (sessionId: string) => void;
  setVerificationToken: (token: string) => void;

  // Step 4-5
  setCarNumber: (carNumber: string) => void;
  setVehicleInfo: (info: VehicleInfo) => void;
  setVehicleOverride: (data: Partial<Pick<EnrollmentState, 'overrideModelYear' | 'overrideValue' | 'hasBlackbox' | 'hasAdvancedSafety'>>) => void;

  // Step 6
  setUsage: (data: Partial<Pick<EnrollmentState, 'mileageDiscount' | 'vehiclePurpose'>>) => void;

  // Step 7
  setDriverScope: (scope: DriverScope) => void;
  setDriverMinAge: (age: number) => void;
  setFamilyMembers: (members: FamilyMember[]) => void;

  // Step 8
  toggleAdjustment: (adj: SelectedAdjustment) => void;
  setSelectedAdjustments: (adjs: SelectedAdjustment[]) => void;

  // Step 9
  setQuote: (quote: QuoteResult | null) => void;

  // Step 10
  setInsured: (data: Partial<PartyInput>) => void;
  setContractor: (data: Partial<PartyInput>) => void;
  setIsSamePerson: (value: boolean) => void;

  // Step 11
  setProposal: (proposalId: string, policyNo?: string) => void;

  reset: () => void;
}

// ─── Initial State ────────────────────────────────────────────────────

const INITIAL_STATE: EnrollmentState = {
  step: 1,
  productId: 0,
  productIdStr: '',
  ownerName: '',
  ownerSsnFront: '',
  ownerSsnBack: '',
  ownerPhone: '',
  consentRequired: { personal: false, thirdParty: false, credit: false, marketing: false },
  consentOptional: false,
  verificationMethod: 'PHONE',
  verificationSessionId: null,
  verificationToken: null,
  carNumber: '',
  vehicleInfo: null,
  overrideModelYear: null,
  overrideValue: null,
  hasBlackbox: false,
  hasAdvancedSafety: false,
  mileageDiscount: false,
  vehiclePurpose: 'COMMUTE',
  driverScope: 'NAMED_ONLY',
  driverMinAge: 0,
  familyMembers: [],
  selectedAdjustments: [],
  quote: null,
  insured: { name: '', ssn: '', phone: '' },
  contractor: { name: '', ssn: '', phone: '' },
  isSamePerson: true,
  proposalId: null,
  policyNo: null,
};

// ─── Store ────────────────────────────────────────────────────────────

export const useEnrollmentStore = create<EnrollmentState & EnrollmentActions>((set) => ({
  ...INITIAL_STATE,

  setStep: (step) => set({ step }),
  nextStep: () => set((s) => ({ step: Math.min(s.step + 1, 11) })),
  prevStep: () => set((s) => ({ step: Math.max(s.step - 1, 1) })),
  setProductId: (productId) => set({ productId }),
  setProductIdStr: (productIdStr) => set({ productIdStr }),

  setOwner: (data) => set(data),

  setConsentRequired: (key, value) =>
    set((s) => ({ consentRequired: { ...s.consentRequired, [key]: value } })),
  setConsentAll: (value) =>
    set((s) => ({
      consentRequired: Object.fromEntries(Object.keys(s.consentRequired).map((k) => [k, value])),
      consentOptional: value,
    })),
  setConsentOptional: (value) => set({ consentOptional: value }),

  setVerificationMethod: (verificationMethod) => set({ verificationMethod }),
  setVerificationSession: (verificationSessionId) => set({ verificationSessionId }),
  setVerificationToken: (verificationToken) => set({ verificationToken }),

  setCarNumber: (carNumber) => set({ carNumber }),
  setVehicleInfo: (vehicleInfo) => set({ vehicleInfo }),
  setVehicleOverride: (data) => set(data),

  setUsage: (data) => set(data),

  setDriverScope: (driverScope) => set({ driverScope }),
  setDriverMinAge: (driverMinAge) => set({ driverMinAge }),
  setFamilyMembers: (familyMembers) => set({ familyMembers }),

  toggleAdjustment: (adj) =>
    set((s) => {
      const exists = s.selectedAdjustments.some((a) => a.itemName === adj.itemName);
      return {
        selectedAdjustments: exists
          ? s.selectedAdjustments.filter((a) => a.itemName !== adj.itemName)
          : [...s.selectedAdjustments, adj],
      };
    }),
  setSelectedAdjustments: (selectedAdjustments) => set({ selectedAdjustments }),

  setQuote: (quote) => set({ quote }),

  setInsured: (data) => set((s) => ({ insured: { ...s.insured, ...data } })),
  setContractor: (data) => set((s) => ({ contractor: { ...s.contractor, ...data } })),
  setIsSamePerson: (isSamePerson) => set({ isSamePerson }),

  setProposal: (proposalId, policyNo) => set({ proposalId, policyNo: policyNo ?? null }),

  reset: () => set(INITIAL_STATE),
}));

/** 편의 훅: store 전체 반환 */
export function useEnrollment() {
  return useEnrollmentStore();
}
