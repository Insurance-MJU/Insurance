'use client';

import { useEffect } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { useAuthStore } from '@/store/authStore';
import { StepHeader } from '@/components/common/ui/StepHeader';
import { StepNavigation } from '@/components/common/ui/StepNavigation';
import VerifyForm from '@/components/enrollment/steps/VerifyForm';

export default function Step03Verify() {
  const { ownerPhone, verificationToken, setVerificationToken, nextStep, prevStep } = useEnrollmentStore();
  const { isVerified, identityToken, setVerified } = useAuthStore();

  useEffect(() => {
    // 이미 인증된 경우 자동 스킵
    if (isVerified && identityToken) {
      if (!verificationToken) setVerificationToken(identityToken);
      nextStep();
      return;
    }
    // enrollmentStore에 토큰이 있으면 스킵
    if (verificationToken) {
      nextStep();
    }
  }, []);

  const handleVerified = (token: string) => {
    setVerificationToken(token);
    setVerified(token); // authStore에도 저장 → 다음에 재인증 불필요
    nextStep();
  };

  return (
    <div className="flex flex-col gap-6">
      <StepHeader
        title="본인인증"
        description={`${ownerPhone} 로 인증번호를 발송합니다`}
      />
      <VerifyForm onVerifiedToken={handleVerified} />
      <StepNavigation onPrev={prevStep} />
    </div>
  );
}
