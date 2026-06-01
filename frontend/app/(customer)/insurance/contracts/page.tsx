'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { fetchMySubscriptions, fetchMyContracts } from '@/queries/contracts';
import type { ContractRow } from '@/types/contract';

const SUB_STATUS: Record<string, { label: string; color: string }> = {
  PENDING_REVIEW:      { label: '심사 중',   color: 'bg-yellow-100 text-yellow-700' },
  SUPPLEMENT_REQUIRED: { label: '보완 요청', color: 'bg-orange-100 text-orange-600' },
  APPROVED:            { label: '승인 완료', color: 'bg-green-100 text-green-700'   },
  REJECTED:            { label: '거절',      color: 'bg-red-100 text-red-600'       },
};

const CONTRACT_STATUS: Record<string, { label: string; color: string }> = {
  PENDING_PAYMENT: { label: '납부 대기', color: 'bg-yellow-100 text-yellow-700' },
  ACTIVE:          { label: '유지 중',  color: 'bg-green-100 text-green-700'   },
  EXPIRED:         { label: '만기',     color: 'bg-gray-100 text-gray-500'     },
  CANCELLED:       { label: '해지',     color: 'bg-red-100 text-red-500'       },
};

export default function MyInsurancePage() {
  const [subs, setSubs]         = useState<ContractRow[]>([]);
  const [contracts, setContracts] = useState<ContractRow[]>([]);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    let mounted = true;
    Promise.all([fetchMySubscriptions(), fetchMyContracts()])
      .then(([s, c]) => { if (mounted) { setSubs(s); setContracts(c); } })
      .catch(() => {})
      .finally(() => { if (mounted) setLoading(false); });
    return () => { mounted = false; };
  }, []);

  return (
    <main className="max-w-2xl mx-auto p-4 space-y-8">

      {/* ── 청약 현황 ──────────────────────────────── */}
      <section>
        <h1 className="text-xl font-bold mb-4">청약 현황</h1>
        {loading ? (
          <p className="text-sm text-gray-400">로딩 중...</p>
        ) : subs.length === 0 ? (
          <p className="text-sm text-gray-400">청약 내역이 없습니다.</p>
        ) : (
          <div className="flex flex-col gap-3">
            {subs.filter(row => row.contractStatus !== 'ACTIVE').map(row => {
              const badge = SUB_STATUS[row.status] ?? { label: row.status, color: 'bg-gray-100 text-gray-600' };
              const canPay = row.status === 'APPROVED' && row.policyNo;
              return (
                <div key={row.id} className="border rounded-lg p-4">
                  <div className="flex justify-between items-start mb-1">
                    <span className="font-semibold text-sm">{row.productName}</span>
                    <span className={`text-xs px-2 py-0.5 rounded font-bold ${badge.color}`}>{badge.label}</span>
                  </div>
                  <p className="text-xs text-gray-400">청약번호: {row.id}</p>
                  <p className="text-xs text-gray-400">청약일: {row.appliedAt}</p>
                  <p className="text-xs text-gray-500 mt-1">월 보험료: {Number(row.premium ?? 0).toLocaleString()}원</p>
                  {canPay && (() => {
                    const initialPremium = Math.round(Number(row.premium ?? 0) * 0.1);
                    return (
                      <>
                        <p className="text-xs text-gray-500">초회보험료: {initialPremium.toLocaleString()}원 (월 보험료의 10%)</p>
                        <Link
                          href={`/payments?subscriptionNo=${row.id}&amount=${initialPremium}`}
                          className="mt-3 inline-block px-4 py-1.5 bg-blue-600 text-white text-sm font-semibold rounded-lg hover:bg-blue-700"
                        >
                          초회보험료 납부
                        </Link>
                      </>
                    );
                  })()}
                </div>
              );
            })}
          </div>
        )}
      </section>

      {/* ── 계약 현황 ──────────────────────────────── */}
      <section>
        <h1 className="text-xl font-bold mb-4">계약 현황</h1>
        {loading ? (
          <p className="text-sm text-gray-400">로딩 중...</p>
        ) : contracts.length === 0 ? (
          <p className="text-sm text-gray-400">계약 내역이 없습니다.</p>
        ) : (
          <div className="flex flex-col gap-3">
            {contracts.map(row => {
              const badge = CONTRACT_STATUS[row.status] ?? { label: row.status, color: 'bg-gray-100 text-gray-600' };
              const isActive = row.status === 'ACTIVE';
              return (
                <div key={row.id} className="border rounded-lg p-4">
                  <div className="flex justify-between items-start mb-1">
                    <span className="font-semibold text-sm">{row.productName}</span>
                    <span className={`text-xs px-2 py-0.5 rounded font-bold ${badge.color}`}>{badge.label}</span>
                  </div>
                  <p className="text-xs text-gray-400">증권번호: {row.policyNo}</p>
                  <p className="text-xs text-gray-400">계약일: {row.appliedAt}</p>
                  <p className="text-xs text-gray-500 mt-1">보험료: {Number(row.premium ?? 0).toLocaleString()}원</p>
                  {isActive && (
                    <div className="flex gap-2 mt-3">
                      <Link href={`/insurance/claims?type=accident&cid=${row.id}`}
                        className="px-3 py-1 bg-orange-100 text-orange-700 text-sm rounded">
                        사고 접수
                      </Link>
                      <Link href={`/insurance/claims?type=payout&cid=${row.id}`}
                        className="px-3 py-1 bg-blue-100 text-blue-700 text-sm rounded">
                        보험금 청구
                      </Link>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </section>

    </main>
  );
}
