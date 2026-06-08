'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { fetchApi } from "@/queries/api";

const STATUS_META: Record<string, { label: string; color: string }> = {
    REPORTED:    { label: "접수",    color: "bg-yellow-100 text-yellow-700" },
    IN_PROGRESS: { label: "처리중",  color: "bg-blue-100 text-blue-600" },
    CLOSED:      { label: "종결",    color: "bg-gray-100 text-gray-500" },
};

export default function AccidentsPage() {
    const [accidents, setAccidents] = useState<any[]>([]);
    const [date, setDate]     = useState("");
    const [status, setStatus] = useState("");
    const [loading, setLoading] = useState(false);

    const load = async () => {
        setLoading(true);
        try {
            const params = new URLSearchParams();
            if (date)   params.set("date", date);
            if (status) params.set("status", status);
            const res = await fetchApi(`/accidents?${params}`);
            setAccidents(Array.isArray(res) ? res : res?.data ?? []);
        } catch { setAccidents([]); }
        finally { setLoading(false); }
    };

    useEffect(() => { load(); }, []);

    return (
        <div className="max-w-5xl space-y-5">
            <div>
                <h1 className="text-xl font-bold text-gray-800">사고 접수 관리</h1>
                <p className="text-sm text-gray-500 mt-0.5">CL-01: 사고 조회 및 현장조사역 배당</p>
            </div>

            {/* 검색 */}
            <div className="bg-white border border-gray-200 rounded-xl p-4 flex gap-3 items-end">
                <div>
                    <label className="block text-xs text-gray-500 mb-1">사고일</label>
                    <input type="date" value={date} onChange={e => setDate(e.target.value)}
                        className="border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                </div>
                <div>
                    <label className="block text-xs text-gray-500 mb-1">상태</label>
                    <select value={status} onChange={e => setStatus(e.target.value)}
                        className="border border-gray-300 rounded-lg px-3 py-2 text-sm">
                        <option value="">전체</option>
                        <option value="REPORTED">접수</option>
                        <option value="IN_PROGRESS">처리중</option>
                        <option value="CLOSED">종결</option>
                    </select>
                </div>
                <button onClick={load} className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    조회
                </button>
            </div>

            {/* 목록 */}
            <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
                <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                    <tr>
                        {["접수번호","신고자","연락처","사고일","사고장소","계약번호","상태",""].map(h => (
                            <th key={h} className="px-4 py-3 text-left">{h}</th>
                        ))}
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                    {loading && <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">조회 중...</td></tr>}
                    {!loading && accidents.length === 0 && (
                        <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">접수된 사고가 없습니다</td></tr>
                    )}
                    {accidents.map((a: any) => {
                        const sm = STATUS_META[a.status] ?? { label: a.status, color: "bg-gray-100 text-gray-600" };
                        return (
                            <tr key={a.accidentId} className="hover:bg-gray-50">
                                <td className="px-4 py-3 font-mono text-xs text-gray-500">{a.accidentId}</td>
                                <td className="px-4 py-3 font-medium">{a.reportedBy}</td>
                                <td className="px-4 py-3 text-gray-500">{a.phone}</td>
                                <td className="px-4 py-3 text-gray-500">{a.accidentDate}</td>
                                <td className="px-4 py-3 text-gray-500">{a.accidentLocation}</td>
                                <td className="px-4 py-3 font-mono text-xs text-gray-500">{a.contractId}</td>
                                <td className="px-4 py-3">
                                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${sm.color}`}>{sm.label}</span>
                                </td>
                                <td className="px-4 py-3 text-right">
                                    <Link href={`/employee/accidents/${a.accidentId}`}
                                        className="text-xs text-blue-500 hover:text-blue-700">상세</Link>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}
