'use client';
import { useEffect } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { calcAgeFromSsn, calcAgeFromBirthYear, ageToMinAgeBracket } from '@/utils/format';
import type { DriverScope } from '@/types/enrollment';

import { StepHeader } from '@/components/common/ui/StepHeader';
import { StepNavigation } from '@/components/common/ui/StepNavigation';
import FamilyMemberForm from '@/components/enrollment/steps/FamilyMemberForm';

import { DRIVER_SCOPES } from '@/types/enrollmentConstants';

export default function Step07DriverScope() {
  const {
    ownerSsnFront, ownerSsnBack,
    driverScope, driverMinAge, familyMembers,
    setDriverScope, setDriverMinAge, setFamilyMembers,
    nextStep, prevStep,
  } = useEnrollmentStore();

  useEffect(() => {
    const ages: number[] = [];
    if (ownerSsnFront.length === 6 && ownerSsnBack.length >= 1) {
      const ownerAge = calcAgeFromSsn(ownerSsnFront, ownerSsnBack[0]);
      if (ownerAge > 0) ages.push(ownerAge);
    }
    if (driverScope === 'COUPLE' || driverScope === 'FAMILY') {
      familyMembers.forEach((m) => {
        const age = calcAgeFromBirthYear(m.birthYear);
        if (age > 0) ages.push(age);
      });
    }
    if (ages.length > 0) {
      const youngest = Math.min(...ages);
      setDriverMinAge(ageToMinAgeBracket(youngest));
    }
  }, [driverScope, familyMembers, ownerSsnFront, ownerSsnBack, setDriverMinAge]);

  const handleScopeChange = (scope: DriverScope) => {
    setDriverScope(scope);
    if (scope === 'NAMED_ONLY' || scope === 'ALL') setFamilyMembers([]);
    if (scope === 'COUPLE' && familyMembers.length > 1) setFamilyMembers(familyMembers.slice(0, 1));
  };

  const minAgeBracketLabel = (bracket: number) => bracket === 0 ? '전연령' : `${bracket}세 이상`;

  return (
    <div className="flex flex-col gap-6">
      <StepHeader 
        title="운전자 범위" 
        description="운전자 범위를 좁힐수록 보험료가 낮아집니다." 
      />

      <div className="flex flex-col gap-2">
        {DRIVER_SCOPES.map((s) => (
          <label
            key={s.value}
            className={`flex items-start gap-3 p-4 border rounded-xl cursor-pointer transition-colors ${
              driverScope === s.value ? 'border-blue-500 bg-blue-50' : 'border-gray-200'
            }`}
          >
            <input
              type="radio"
              name="scope"
              checked={driverScope === s.value}
              onChange={() => handleScopeChange(s.value)}
              className="mt-0.5 accent-blue-500"
            />
            <div>
              <p className="text-sm font-medium">{s.label}</p>
              <p className="text-xs text-gray-500">{s.desc}</p>
            </div>
          </label>
        ))}
      </div>

      <FamilyMemberForm />

      <div className="bg-blue-50 rounded-xl p-4">
        <div className="flex items-center justify-between">
          <span className="text-sm font-medium">운전자 최소연령 (자동 설정)</span>
          <span className="text-sm font-bold text-blue-600">{minAgeBracketLabel(driverMinAge)}</span>
        </div>
        <p className="text-xs text-gray-500 mt-1">
          {driverScope === 'NAMED_ONLY'
            ? `본인(만 ${ownerSsnFront.length === 6 && ownerSsnBack.length >= 1 ? calcAgeFromSsn(ownerSsnFront, ownerSsnBack[0]) : '?'}세) 기준으로 자동 설정됩니다.`
            : '등록된 운전자 중 가장 어린 사람 기준으로 자동 설정됩니다.'}
        </p>
      </div>

      <StepNavigation onPrev={prevStep} onNext={nextStep} />
    </div>
  );
}
