import React from 'react';
import { useRouter } from 'next/navigation';
import { formatMoney } from '@/utils/format';

interface ConfirmResultViewProps {
  status: string | null;
  polling: boolean;
  policyNo?: string;
  quoteTotalPremium?: number;
}

export function ConfirmResultView({ status, polling, policyNo, quoteTotalPremium }: ConfirmResultViewProps) {
  const router = useRouter();

  if (polling || status === 'CREDIT_CHECKED' || status === 'UNDERWRITING') {
    return (
      <div className="flex flex-col items-center justify-center py-16 gap-4">
        <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
        <p className="text-base font-semibold">청약 심사 진행 중</p>
        <p className="text-sm text-gray-500">
          {status === 'SUBMITTED' && '청약 접수 완료, 신용조회 중...'}
          {status === 'CREDIT_CHECKED' && '신용조회 완료, 인수심사 중...'}
          {status === 'UNDERWRITING' && '언더라이팅 처리 중...'}
        </p>
      </div>
    );
  }

  if (status === 'APPROVED') {
    return (
      <div className="flex flex-col items-center text-center gap-6 py-8">
        <div className="text-5xl">🎉</div>
        <div>
          <h2 className="text-2xl font-bold text-blue-600">가입 완료</h2>
          <p className="text-gray-500 mt-1">보험 가입이 정상 완료되었습니다.</p>
        </div>
        <div className="w-full bg-blue-50 rounded-xl p-5 text-left">
          <p className="text-sm text-gray-500 mb-1">증권번호</p>
          <p className="text-lg font-bold">{policyNo}</p>
          <p className="text-sm text-gray-500 mt-3 mb-1">최종 보험료 (일시납)</p>
          <p className="text-lg font-bold text-blue-600">{formatMoney(quoteTotalPremium || 0)}</p>
        </div>
        <button onClick={() => router.push('/insurance/contracts')} className="w-full bg-blue-500 text-white font-semibold p-4 rounded-xl">
          계약 내역 확인
        </button>
      </div>
    );
  }

  if (status === 'SUPPLEMENT_REQUIRED') {
    return (
      <div className="flex flex-col items-center text-center gap-6 py-8">
        <div className="text-5xl">📋</div>
        <h2 className="text-xl font-bold">서류 보완 필요</h2>
        <div className="w-full bg-yellow-50 border border-yellow-200 rounded-xl p-4 text-left text-sm">
          <p className="font-semibold text-yellow-700 mb-1">안내 사항</p>
          <p className="text-gray-600">운전경력 증빙서류를 고객센터 또는 앱에서 제출해 주세요.</p>
        </div>
        <button onClick={() => router.push('/insurance/contracts')} className="w-full border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl">
          계약 내역으로 이동
        </button>
      </div>
    );
  }

  if (status === 'REJECTED') {
    return (
      <div className="flex flex-col items-center text-center gap-6 py-8">
        <div className="text-5xl">❌</div>
        <h2 className="text-xl font-bold">인수 거절</h2>
        <p className="text-sm text-gray-500">현재 조건으로는 보험 가입이 어렵습니다.</p>
        <button onClick={() => router.push('/insurance/products')} className="w-full border border-gray-300 text-gray-700 font-semibold p-4 rounded-xl">
          다른 상품 보기
        </button>
      </div>
    );
  }

  return null;
}
