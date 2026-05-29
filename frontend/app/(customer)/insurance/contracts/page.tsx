'use client';

import { useEffect, useMemo, useState } from 'react';
import Link from 'next/link';
import { fetchMyContracts } from '@/queries/contracts';
import { CONTRACT_STATUS_META } from '@/types/contract';
import type { ContractRow } from '@/types/contract';

export default function ContractsPage() {
  const [rows, setRows] = useState<ContractRow[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    (async () => {
      setLoading(true);
      try {
        const data = await fetchMyContracts();
        if (mounted) setRows(data);
      } catch {
        if (mounted) setRows([]);
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  const activeRows = useMemo(() => rows.filter((c) => c.status === 'ACTIVE'), [rows]);

  return (
    <main className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-6">내 보험 계약 현황</h1>
      <div className="flex flex-col gap-4">
        {loading ? (
          <div className="border rounded-lg p-6 text-center text-gray-400 text-sm">로딩 중...</div>
        ) : rows.length === 0 ? (
          <div className="border rounded-lg p-6 text-center text-gray-400 text-sm">계약 데이터가 없습니다.</div>
        ) : (
          rows.map((contract) => {
            const badge = CONTRACT_STATUS_META[contract.status] ?? { label: contract.status, color: 'bg-gray-100 text-gray-600' };
            return (
              <div key={contract.id} className="border rounded-lg p-5">
                <div className="flex justify-between items-start mb-2">
                  <h2 className="text-lg font-bold">{contract.productName}</h2>
                  <span className={`px-2 py-1 text-xs rounded font-bold ${badge.color}`}>{badge.label}</span>
                </div>
                <p className="text-sm text-gray-500 mb-1">증권번호: {contract.policyNo}</p>
                <p className="text-sm text-gray-500 mb-1">청약일: {contract.appliedAt}</p>
                <p className="text-sm text-gray-500 mb-4">보험료: {Number(contract.premium ?? 0).toLocaleString()}원</p>

                <div className="flex flex-wrap gap-2 mt-4 border-t pt-4">
                  {(contract.status === 'ACTIVE' || contract.status === 'APPROVED') && (
                    <>
                      <Link href={`/insurance/claims?type=accident&cid=${contract.id}`} className="px-3 py-1 bg-orange-100 text-orange-700 text-sm rounded">
                        사고 접수
                      </Link>
                      <Link href={`/insurance/claims?type=payout&cid=${contract.id}`} className="px-3 py-1 bg-blue-100 text-blue-700 text-sm rounded">
                        보험금 청구
                      </Link>
                    </>
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>

      {!loading && activeRows.length === 0 && rows.length > 0 && (
        <p className="text-xs text-gray-400 mt-3">현재 보상/청구 가능한 ACTIVE 계약이 없습니다.</p>
      )}
    </main>
  );
}
