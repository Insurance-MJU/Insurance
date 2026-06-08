'use client';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { useProposalSubmit } from '@/hooks/useProposalSubmit';
import { formatMoney, maskSsn } from '@/utils/format';
import { StepHeader } from '@/components/common/ui/StepHeader';
import { DataSection, DataRow } from '@/components/common/ui/DataViewer';
import { ConfirmResultView } from '@/components/enrollment/steps/ConfirmResultView';

export default function Step11Confirm() {
  const { prevStep } = useEnrollmentStore();
  const { 
    handleSubmit, submitting, polling, status, error,
    quote, policyNo, isSamePerson, insured, contractor,
    carNumber, vehicleInfo, driverScope
  } = useProposalSubmit();
  const driverMinAge = useEnrollmentStore(s => s.driverMinAge);

  if (!quote) return null;

  if (polling || status) {
    return <ConfirmResultView status={status} polling={polling} policyNo={policyNo ?? undefined} quoteTotalPremium={quote?.totalPremium} />;
  }

  const contractorInfo = isSamePerson ? insured : contractor;
  const scopeLabel: Record<string, string> = { NAMED_ONLY: '본인 한정', COUPLE: '부부 한정', FAMILY: '가족 한정', ALL: '누구나' };

  return (
    <div className="flex flex-col gap-6">
      <StepHeader title="청약 내용 확인" description="최종 내용을 확인하고 청약하기를 눌러주세요." />

      <DataSection title="차량 정보">
        <DataRow label="차량번호" value={carNumber} />
        <DataRow label="차종" value={`${vehicleInfo?.modelName} (${vehicleInfo?.modelYear})`} />
        <DataRow label="운전자 범위" value={scopeLabel[driverScope]} />
        <DataRow label="최소 연령" value={driverMinAge === 0 ? '전연령' : `${driverMinAge}세 이상`} />
      </DataSection>

      <DataSection title="피보험자">
        <DataRow label="이름" value={insured.name} />
        <DataRow label="주민번호" value={maskSsn(insured.ssn.replace(/-/g, ''))} />
        <DataRow label="연락처" value={insured.phone} />
      </DataSection>

      {!isSamePerson && (
        <DataSection title="계약자">
          <DataRow label="이름" value={contractorInfo.name} />
          <DataRow label="연락처" value={contractorInfo.phone} />
        </DataSection>
      )}

      <DataSection title="보험료 (일시납)">
        <DataRow label="할인 전" value={formatMoney(quote.totalBeforeDiscount)} />
        <DataRow label="할인 합계" value={formatMoney(quote.totalDiscount)} />
        <div className="flex justify-between items-center px-4 py-3 bg-blue-50">
          <span className="text-sm font-bold">최종 보험료</span>
          <span className="text-base font-bold text-blue-600">{formatMoney(quote.totalPremium)}</span>
        </div>
      </DataSection>

      {error && <p className="text-sm text-red-500">{error}</p>}

      <div className="flex gap-3">
        <button onClick={prevStep} className="flex-1 border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl">이전</button>
        <button onClick={handleSubmit} disabled={submitting} className="flex-1 bg-blue-500 disabled:bg-gray-300 text-white font-semibold p-4 rounded-xl">
          {submitting ? '제출 중...' : '청약하기'}
        </button>
      </div>
    </div>
  );
}
