'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { REQUIRED_CONSENT_ITEMS } from '@/types/enrollmentConstants';

export default function Step02Consent() {
  const { consentRequired, consentOptional, setConsentRequired, setConsentAll, setConsentOptional, nextStep, prevStep } =
    useEnrollmentStore();

  const allRequiredChecked = REQUIRED_CONSENT_ITEMS.every((item) => consentRequired[item.key]);
  const isValid = allRequiredChecked;

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-xl font-bold">개인정보 동의</h2>
        <p className="text-sm text-gray-500 mt-1">보험 가입을 위해 아래 항목에 동의해 주세요.</p>
      </div>

      <div className="border rounded-xl overflow-hidden">
        <label className="flex items-center gap-3 p-4 bg-gray-50 cursor-pointer">
          <input
            type="checkbox"
            checked={allRequiredChecked && consentOptional}
            onChange={(e) => setConsentAll(e.target.checked)}
            className="w-5 h-5 accent-blue-500"
          />
          <span className="font-semibold text-sm">전체 동의</span>
        </label>
        <div className="divide-y">
          {REQUIRED_CONSENT_ITEMS.map((item) => (
            <label key={item.key} className="flex items-center gap-3 p-4 cursor-pointer">
              <input
                type="checkbox"
                checked={!!consentRequired[item.key]}
                onChange={(e) => setConsentRequired(item.key, e.target.checked)}
                className="w-5 h-5 accent-blue-500"
              />
              <span className="text-sm">{item.label}</span>
            </label>
          ))}
          <label className="flex items-center gap-3 p-4 cursor-pointer">
            <input
              type="checkbox"
              checked={consentOptional}
              onChange={(e) => setConsentOptional(e.target.checked)}
              className="w-5 h-5 accent-blue-500"
            />
            <span className="text-sm text-gray-500">[선택] 보험 상품 안내 및 혜택 알림 수신 동의</span>
          </label>
        </div>
      </div>

      <div className="flex gap-3">
        <button onClick={prevStep} className="flex-1 border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl">
          이전
        </button>
        <button disabled={!isValid} onClick={nextStep} className="flex-1 bg-blue-500 disabled:bg-gray-300 text-white font-semibold p-4 rounded-xl">
          다음
        </button>
      </div>
    </div>
  );
}
