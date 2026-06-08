'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import Step01OwnerInfo from './steps/Step01OwnerInfo';
import Step02Consent from './steps/Step02Consent';
import Step03Verify from './steps/Step03Verify';
import Step04Vehicle from './steps/Step04Vehicle';
import Step05VehicleDetail from './steps/Step05VehicleDetail';
import Step06Usage from './steps/Step06Usage';
import Step07DriverScope from './steps/Step07DriverScope';
import Step08Discounts from './steps/Step08Discounts';
import Step09Quote from './steps/Step09Quote';
import Step10Parties from './steps/Step10Parties';
import Step11Confirm from './steps/Step11Confirm';

const STEP_LABELS = [
  '소유자 정보', '개인정보 동의', '본인인증', '차량번호', '차량 확인',
  '마일리지·용도', '운전자 범위', '할인특약', '보험료 확인', '계약자 정보', '청약 확인',
];

const STEP_COMPONENTS = [
  Step01OwnerInfo, Step02Consent, Step03Verify, Step04Vehicle, Step05VehicleDetail,
  Step06Usage, Step07DriverScope, Step08Discounts, Step09Quote, Step10Parties, Step11Confirm,
];

export default function EnrollmentWizard() {
  const step = useEnrollmentStore((s) => s.step);
  const idx = step - 1;
  const StepComponent = STEP_COMPONENTS[idx];

  return (
    <div className="max-w-xl mx-auto p-4">
      {/* 프로그레스 바 */}
      <div className="mb-6">
        <div className="flex justify-between text-xs text-gray-400 mb-1">
          <span>Step {step} / 11</span>
          <span>{STEP_LABELS[idx]}</span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-1.5">
          <div
            className="bg-blue-500 h-1.5 rounded-full transition-all duration-300"
            style={{ width: `${(step / 11) * 100}%` }}
          />
        </div>
        <div className="flex justify-between mt-2">
          {STEP_LABELS.map((_, i) => (
            <div
              key={i}
              className={`w-2 h-2 rounded-full transition-colors ${
                i + 1 < step ? 'bg-blue-500' : i + 1 === step ? 'bg-blue-600 ring-2 ring-blue-200' : 'bg-gray-200'
              }`}
            />
          ))}
        </div>
      </div>

      {StepComponent && <StepComponent />}
    </div>
  );
}
