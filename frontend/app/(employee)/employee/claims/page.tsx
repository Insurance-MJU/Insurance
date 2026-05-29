'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { fetchApi } from "@/queries/api";

const STATUS_META: Record<string, { label: string; color: string }> = {
    PENDING:         { label: "대기",       color: "bg-gray-100 text-gray-500" },
    INVESTIGATING:   { label: "조사중",     color: "bg-yellow-100 text-yellow-700" },
    ASSESSING:       { label: "산정중",     color: "bg-orange-100 text-orange-600" },
    PAYMENT_PENDING: { label: "지급대기",   color: "bg-blue-100 text-blue-600" },
    CLOSED:          { label: "종결",       color: "bg-green-100 text-green-700" },
};

export default function ClaimsPage() {
    const [claims, setClaims] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchApi("/claims")
            .then(r => setClaims(Array.isArray(r) ? r : r?.data ?? []))
            .catch(() => setClaims([]))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="max-w-5xl space-y-5">
            <div>
                <h1 className="text-xl font-bold text-gray-800">손해 처리 관리</h1>
                <p className="text-sm text-gray-500 mt-0.5">CL-02: 손해액 산정 · CL-04: 보험금 지급</p>
            </div>

            <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
                <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                    <tr>
                        {["클레임번호","고객명","사고번호","계약번호","담당직원","접수일","상태",""].map(h => (
                            <th key={h} className="px-4 py-3 text-left">{h}</th>
                        ))}
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                    {loading && <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">로딩 중...</td></tr>}
                    {!loading && claims.length === 0 && (
                        <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">처리 대기 중인 클레임이 없습니다</td></tr>
                    )}
                    {claims.map((c: any) => {
                        const sm = STATUS_META[c.status] ?? { label: c.status, color: "bg-gray-100 text-gray-600" };
                        return (
                            <tr key={c.claimId} className="hover:bg-gray-50">
                                <td className="px-4 py-3 font-mono text-xs text-gray-500">{c.claimId}</td>
                                <td className="px-4 py-3 font-medium">{c.customerName}</td>
                                <td className="px-4 py-3 font-mono text-xs text-gray-500">{c.accidentId}</td>
                                <td className="px-4 py-3 font-mono text-xs text-gray-500">{c.contractId}</td>
                                <td className="px-4 py-3 text-gray-500">{c.assignedEmployee ?? "-"}</td>
                                <td className="px-4 py-3 text-gray-500">{c.receivedDate}</td>
                                <td className="px-4 py-3">
                                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${sm.color}`}>{sm.label}</span>
                                </td>
                                <td className="px-4 py-3 text-right">
                                    <Link href={`/employee/claims/${c.claimId}`}
                                        className="text-xs text-blue-500 hover:text-blue-700">처리</Link>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}
