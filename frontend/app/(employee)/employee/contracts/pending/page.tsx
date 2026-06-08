'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { fetchPendingContracts } from "@/queries/contracts";
import type { ContractRow } from "@/types/contract";
import { CONTRACT_STATUS_META } from "@/types/contract";

export default function PendingContractsPage() {
  const [rows, setRows] = useState<ContractRow[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPendingContracts()
      .then(setRows)
      .catch(() => setRows([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="max-w-5xl space-y-5">
      <div>
        <h1 className="text-xl font-bold text-gray-800">청약 심사</h1>
        <p className="text-sm text-gray-500 mt-0.5">심사 대기 중인 청약 목록입니다. 항목을 클릭하여 위험성 분석 후 심사를 진행하세요.</p>
      </div>

      <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-xs text-gray-500">
            <tr>
              {["청약번호", "청약자", "상품명", "보험료", "청약일", "상태", ""].map(h => (
                <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              <tr><td colSpan={7} className="px-4 py-12 text-center text-gray-400 text-sm">로딩 중...</td></tr>
            ) : rows.length === 0 ? (
              <tr><td colSpan={7} className="px-4 py-12 text-center text-gray-400 text-sm">심사 대기 청약이 없습니다</td></tr>
            ) : (
              rows.map(c => {
                const badge = CONTRACT_STATUS_META[c.status] ?? { label: c.status, color: "bg-gray-100 text-gray-600" };
                return (
                  <tr key={c.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-mono text-xs text-gray-500">{c.proposalId}</td>
                    <td className="px-4 py-3 font-medium">{c.insuredName}</td>
                    <td className="px-4 py-3 text-gray-600">{c.productName}</td>
                    <td className="px-4 py-3 text-right">{(c.premium ?? 0).toLocaleString()} KRW</td>
                    <td className="px-4 py-3 text-xs text-gray-400">{c.appliedAt}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${badge.color}`}>{badge.label}</span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <Link href={`/employee/contracts/pending/${c.id}`}
                        className="text-xs text-blue-500 hover:text-blue-700 font-medium">
                        심사하기 →
                      </Link>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
