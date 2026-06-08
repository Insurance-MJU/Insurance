'use client';
import { useEffect } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { formatMoney } from '@/utils/format';
import { useQuoteCalculation } from '@/hooks/useQuoteCalculation';
import { StepHeader } from '@/components/common/ui/StepHeader';
import { StepNavigation } from '@/components/common/ui/StepNavigation';
import { DataSection, DataRow } from '@/components/common/ui/DataViewer';

export default function Step09Quote() {
  const { nextStep, prevStep } = useEnrollmentStore();
  const { quote, loading, error, fetchQuote, resetQuote } = useQuoteCalculation();

  useEffect(() => {
    if (!quote) fetchQuote();
  }, [quote, fetchQuote]);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-16 gap-4">
        <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
        <p className="text-sm text-gray-500">보험료를 계산하고 있습니다...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col gap-4">
        <p className="text-red-500 text-sm">{error}</p>
        <button onClick={fetchQuote} className="w-full bg-blue-500 text-white p-4 rounded-xl font-semibold">다시 계산</button>
        <button onClick={prevStep} className="w-full border border-gray-300 text-gray-700 p-4 rounded-xl font-semibold">이전</button>
      </div>
    );
  }

  if (!quote) return null;

  return (
    <div className="flex flex-col gap-6">
      <StepHeader 
        title="보험료 확인" 
        description="선택한 조건에 따른 보험료입니다." 
      />

      <DataSection title="담보별 보험료">
        {quote.coverages.map((c: any, i: number) => (
          <DataRow 
            key={i} 
            label={c.name} 
            value={formatMoney(c.premium)} 
          />
        ))}
      </DataSection>

      {quote.appliedDiscounts.length > 0 && (
        <DataSection title="적용 할인" headerClassName="bg-green-50 text-green-700">
          {quote.appliedDiscounts.map((d: any, i: number) => (
            <DataRow 
              key={i} 
              label={d.name} 
              value={formatMoney(d.amount)} 
              valueClassName="text-green-600" 
            />
          ))}
        </DataSection>
      )}

      <div className="bg-blue-50 rounded-xl p-5">
        <div className="flex justify-between text-sm text-gray-500 mb-1">
          <span>할인 전 보험료</span>
          <span>{formatMoney(quote.totalBeforeDiscount)}</span>
        </div>
        <div className="flex justify-between text-sm text-green-600 mb-3">
          <span>할인 합계</span>
          <span>{formatMoney(quote.totalDiscount)}</span>
        </div>
        <div className="flex justify-between items-baseline">
          <span className="font-semibold">최종 보험료</span>
          <span className="text-2xl font-bold text-blue-600">{formatMoney(quote.totalPremium)}</span>
        </div>
        <p className="text-xs text-gray-400 mt-1 text-right">일시납 기준</p>
      </div>

      <StepNavigation 
        onPrev={prevStep} 
        onNext={nextStep} 
        extraButton={
          <button
            onClick={() => { resetQuote(); fetchQuote(); }}
            className="border border-gray-300 text-gray-600 px-4 py-3 rounded-xl text-sm"
          >
            재계산
          </button>
        }
      />
    </div>
  );
}
