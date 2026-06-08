'use client';
import { useEffect } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { maskSsn } from '@/utils/format';
import PartyForm from '@/components/enrollment/PartyForm';
import { StepHeader } from '@/components/common/ui/StepHeader';
import { StepNavigation } from '@/components/common/ui/StepNavigation';

export default function Step10Parties() {
  const {
    ownerName, ownerSsnFront, ownerSsnBack, ownerPhone,
    insured, contractor, isSamePerson,
    setInsured, setContractor, setIsSamePerson,
    nextStep, prevStep,
  } = useEnrollmentStore();

  useEffect(() => {
    if (!insured.name && ownerName) {
      setInsured({
        name: ownerName,
        ssn: ownerSsnFront && ownerSsnBack ? `${ownerSsnFront}-${ownerSsnBack}` : '',
        phone: ownerPhone,
      });
    }
  }, [insured.name, ownerName, ownerSsnFront, ownerSsnBack, ownerPhone, setInsured]);

  const isInsuredValid =
    insured.name.trim().length >= 2 &&
    insured.ssn.replace(/-/g, '').length === 13 &&
    insured.phone.replace(/-/g, '').length >= 10;

  const isContractorValid =
    isSamePerson ||
    (contractor.name.trim().length >= 2 &&
      contractor.ssn.replace(/-/g, '').length === 13 &&
      contractor.phone.replace(/-/g, '').length >= 10);

  const isValid = isInsuredValid && isContractorValid;

  return (
    <div className="flex flex-col gap-6">
      <StepHeader 
        title="계약자·피보험자 정보" 
        description="보험계약에 필요한 당사자 정보를 확인·입력해 주세요." 
      />

      <section>
        <h3 className="text-sm font-semibold text-gray-700 mb-3">피보험자 (차량 소유자)</h3>
        <PartyForm
          value={insured}
          onChange={setInsured}
          placeholder={{ name: ownerName, phone: ownerPhone }}
        />
      </section>

      <label className="flex items-center gap-3 p-4 border rounded-xl cursor-pointer">
        <input
          type="checkbox"
          checked={isSamePerson}
          onChange={(e) => setIsSamePerson(e.target.checked)}
          className="w-5 h-5 accent-blue-500"
        />
        <div>
          <p className="text-sm font-medium">계약자와 피보험자가 동일합니다</p>
          <p className="text-xs text-gray-400">보험료를 납부하는 계약자가 피보험자와 같은 경우 선택</p>
        </div>
      </label>

      {!isSamePerson && (
        <section>
          <h3 className="text-sm font-semibold text-gray-700 mb-3">계약자 (보험료 납부자)</h3>
          <PartyForm value={contractor} onChange={setContractor} />
        </section>
      )}

      <StepNavigation onPrev={prevStep} onNext={nextStep} nextDisabled={!isValid} />
    </div>
  );
}
