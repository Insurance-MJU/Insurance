'use client';

import { useState } from "react";
import { fetchApi } from "@/queries/api";
import { CONTRACT_STATUS_META } from "@/types/contract";

export default function ContractSearchPage() {
    const [query, setQuery]     = useState({ policyNo: "", name: "", productName: "", status: "" });
    const [results, setResults] = useState<any[] | null>(null);
    const [loading, setLoading] = useState(false);
    const [apiError, setApiError] = useState("");

    const handleSearch = async () => {
        setLoading(true);
        setApiError("");
        try {
            const res = await fetchApi("/subscriptions");
            const all: any[] = Array.isArray(res) ? res : (res?.data ?? []);

            const filtered = all.filter(c => {
                const no      = (c.subscriptionNo ?? "").toLowerCase();
                const name    = (c.applicantName  ?? "").toLowerCase();
                const product = (c.productName    ?? "").toLowerCase();
                const status  = c.status ?? "";

                if (query.policyNo    && !no.includes(query.policyNo.toLowerCase()))         return false;
                if (query.name        && !name.includes(query.name.toLowerCase()))           return false;
                if (query.productName && !product.includes(query.productName.toLowerCase())) return false;
                if (query.status      && status !== query.status)                             return false;
                return true;
            });

            setResults(filtered);
        } catch (e: any) {
            setApiError(e?.message ?? "서버 오류가 발생했습니다. 백엔드 서버를 확인하세요.");
            setResults([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-4xl space-y-5">
            <div>
                <h1 className="text-xl font-bold text-gray-800">계약 조회</h1>
                <p className="text-sm text-gray-500 mt-0.5">청약번호, 청약자명, 상품명으로 검색합니다.</p>
            </div>

            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                        <label className="block text-xs text-gray-500 mb-1">청약번호</label>
                        <input value={query.policyNo}
                            onChange={e => setQuery(q => ({ ...q, policyNo: e.target.value }))}
                            placeholder="예) 20260418-0001"
                            className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-1">청약자명</label>
                        <input value={query.name}
                            onChange={e => setQuery(q => ({ ...q, name: e.target.value }))}
                            placeholder="청약자 이름"
                            className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-1">상품명</label>
                        <input value={query.productName}
                            onChange={e => setQuery(q => ({ ...q, productName: e.target.value }))}
                            placeholder="보험 상품명"
                            className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    <div>
                        <label className="block text-xs text-gray-500 mb-1">계약 상태</label>
                        <select value={query.status}
                            onChange={e => setQuery(q => ({ ...q, status: e.target.value }))}
                            className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                            <option value="">전체</option>
                            <option value="PENDING_REVIEW">심사 중</option>
                            <option value="APPROVED">승인</option>
                            <option value="REJECTED">거절</option>
                            <option value="SUPPLEMENT_REQUIRED">보완요청</option>
                        </select>
                    </div>
                </div>
                {apiError && (
                    <p className="mb-3 text-sm text-red-500 bg-red-50 rounded-lg px-3 py-2">{apiError}</p>
                )}
                <div className="flex justify-end">
                    <button onClick={handleSearch} disabled={loading}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-40">
                        {loading ? "조회 중..." : "조회"}
                    </button>
                </div>
            </div>

            {results !== null && (
                <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
                    <table className="w-full text-sm">
                        <thead className="bg-gray-50 text-xs text-gray-500">
                            <tr>
                                {["청약번호", "청약자", "상품명", "보험료", "청약일", "상태"].map(h => (
                                    <th key={h} className="px-4 py-3 text-left font-medium">{h}</th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            {results.length === 0 ? (
                                <tr>
                                    <td colSpan={6} className="px-4 py-10 text-center text-gray-400 text-sm">
                                        검색 결과가 없습니다
                                    </td>
                                </tr>
                            ) : results.map((c: any) => {
                                const badge = CONTRACT_STATUS_META[c.status] ?? { label: c.status, color: "bg-gray-100 text-gray-600" };
                                return (
                                    <tr key={c.subscriptionNo} className="border-t border-gray-100 hover:bg-gray-50">
                                        <td className="px-4 py-3 font-mono text-xs">{c.subscriptionNo}</td>
                                        <td className="px-4 py-3">{c.applicantName}</td>
                                        <td className="px-4 py-3 text-gray-600">{c.productName}</td>
                                        <td className="px-4 py-3 text-right">{c.premium?.toLocaleString()}원</td>
                                        <td className="px-4 py-3 text-xs text-gray-400">{c.subscriptionDate}</td>
                                        <td className="px-4 py-3">
                                            <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${badge.color}`}>{badge.label}</span>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
