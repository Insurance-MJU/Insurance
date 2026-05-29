'use client';

import { useEffect, useMemo, useState } from "react";
import { fetchAllContracts } from "@/queries/contracts";
import { CONTRACT_STATUS_META } from "@/types/contract";
import type { ContractRow } from "@/types/contract";

export default function ContractListPage() {
  const [tab, setTab] = useState<"ALL" | keyof typeof CONTRACT_STATUS_META>("ALL");
  const [rows, setRows] = useState<ContractRow[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let mounted = true;
    (async () => {
      setLoading(true);
      try {
        const data = await fetchAllContracts();
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

  const filtered = useMemo(
    () => (tab === "ALL" ? rows : rows.filter((c) => c.status === tab)),
    [rows, tab]
  );

  return (
    <div className="max-w-5xl space-y-5">
      <div>
        <h1 className="text-xl font-bold text-gray-800">Contract List</h1>
        <p className="text-sm text-gray-500 mt-0.5">All contracts from proposal flow</p>
      </div>

      <div className="flex gap-1 border-b border-gray-200">
        {(["ALL", "PENDING_REVIEW", "APPROVED", "REJECTED", "SUPPLEMENT_REQUIRED"] as const).map((s) => (
          <button
            key={s}
            onClick={() => setTab(s as any)}
            className={`px-4 py-2 text-xs font-medium transition-colors border-b-2 -mb-px ${
              tab === s
                ? "border-blue-600 text-blue-600"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            {s === "ALL" ? "전체" : CONTRACT_STATUS_META[s]?.label ?? s}
          </button>
        ))}
      </div>

      <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-xs text-gray-500">
            <tr>
              {["Policy No", "Insured", "Product", "Premium", "Applied At", "Status"].map((h) => (
                <th key={h} className="px-4 py-3 text-left font-medium">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading ? (
              <tr>
                <td colSpan={6} className="px-4 py-12 text-center text-gray-400 text-sm">
                  Loading...
                </td>
              </tr>
            ) : filtered.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-4 py-12 text-center text-gray-400 text-sm">
                  No contracts found
                </td>
              </tr>
            ) : (
              filtered.map((c) => {
                const badge = CONTRACT_STATUS_META[c.status] ?? { label: c.status, color: "bg-gray-100 text-gray-500" };
                return (
                  <tr key={c.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-mono text-xs text-gray-500">{c.policyNo}</td>
                    <td className="px-4 py-3 font-medium">{c.insuredName}</td>
                    <td className="px-4 py-3 text-gray-600">{c.productName}</td>
                    <td className="px-4 py-3 text-right">{(c.premium ?? 0).toLocaleString()} KRW</td>
                    <td className="px-4 py-3 text-xs text-gray-400">{c.appliedAt}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${badge.color}`}>{badge.label}</span>
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
