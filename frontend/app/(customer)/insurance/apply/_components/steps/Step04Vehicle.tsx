'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { useVehicleInquiry } from '@/hooks/useVehicleInquiry';
import { StepHeader } from '@/components/common/ui/StepHeader';
import { StepNavigation } from '@/components/common/ui/StepNavigation';

export default function Step04Vehicle() {
  const { carNumber, setCarNumber, prevStep } = useEnrollmentStore();
  const { handleInquire, loading, error, setError } = useVehicleInquiry();

  return (
    <div className="flex flex-col gap-6">
      <StepHeader 
        title="차량번호 입력" 
        description="보험에 가입할 차량 번호를 입력해 주세요." 
      />

      <div>
        <label className="block text-sm font-medium mb-1">차량번호</label>
        <div className="flex gap-2">
          <input
            type="text"
            className="flex-1 border rounded-lg p-3 text-sm text-center text-lg tracking-wider"
            placeholder="예) 12가 3456"
            value={carNumber}
            onChange={(e) => { setCarNumber(e.target.value); setError(''); }}
            onKeyDown={(e) => e.key === 'Enter' && handleInquire()}
          />
          <button
            onClick={handleInquire}
            disabled={!carNumber.trim() || loading}
            className="bg-blue-500 disabled:bg-gray-300 text-white px-5 rounded-lg text-sm font-semibold whitespace-nowrap"
          >
            {loading ? '조회 중...' : '조회'}
          </button>
        </div>
        {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
      </div>

      <div className="bg-blue-50 rounded-xl p-4 text-sm text-blue-700">
        <p className="font-medium mb-1">차량번호 형식</p>
        <p>예) 12가3456 · 서울12가3456 · 전기차 123가4567</p>
      </div>

      <StepNavigation onPrev={prevStep} />
    </div>
  );
}
