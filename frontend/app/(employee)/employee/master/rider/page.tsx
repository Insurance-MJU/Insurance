'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getRiders, deleteRider } from "@/queries/master";

const TYPE_BADGE: Record<string, string> = {
    DISCOUNT: "bg-green-100 text-green-700",
    ADD_ON:   "bg-blue-100 text-blue-700",
};

export default function RiderListPage() {
    const [list, setList] = useState<any[]>([]);
    const [error, setError] = useState("");

    const load = async () => {
        try { setList(await getRiders()); }
        catch { setError("목록 로드 실패"); }
    };
    useEffect(() => { load(); }, []);

    return (
        <div className="max-w-4xl">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-xl font-bold text-gray-800">특약 마스터</h1>
                    <p className="text-sm text-gray-500">할인 특약·보장 추가 특약을 관리합니다.</p>
                </div>
                <Link href="/employee/master/rider/new"
                    className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    + 특약 등록
                </Link>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
                <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                    <tr>
                        {["유형", "특약 코드", "특약명", "할인율", "면책사항", ""].map(h => (
                            <th key={h} className="px-4 py-3 text-left">{h}</th>
                        ))}
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                    {list.length === 0 && (
                        <tr><td colSpan={6} className="px-4 py-8 text-center text-gray-400">등록된 특약이 없습니다</td></tr>
                    )}
                    {list.map((r: any) => (
                        <tr key={r.id} className="hover:bg-gray-50">
                            <td className="px-4 py-3">
                                <span className={`px-2 py-0.5 rounded text-xs font-medium ${TYPE_BADGE[r.riderType] ?? "bg-gray-100 text-gray-600"}`}>
                                    {r.riderTypeDisplayName}
                                </span>
                            </td>
                            <td className="px-4 py-3 text-gray-500 font-mono text-xs">{r.riderCode}</td>
                            <td className="px-4 py-3 font-medium">
                                <Link href={`/employee/master/rider/${r.id}`} className="hover:text-blue-600">
                                    {r.name}
                                </Link>
                                {r.mandatory && <span className="ml-2 px-1.5 py-0.5 bg-blue-100 text-blue-700 rounded text-xs">의무</span>}
                            </td>
                            <td className="px-4 py-3 text-gray-600 text-xs">
                                {r.discountRate != null ? `${(r.discountRate * 100).toFixed(1)}%` : "-"}
                            </td>
                            <td className="px-4 py-3 text-gray-500 text-xs">
                                {r.exclusions?.length > 0 ? `${r.exclusions.length}건` : "-"}
                            </td>
                            <td className="px-4 py-3 text-right">
                                <Link href={`/employee/master/rider/${r.id}/edit`}
                                    className="text-xs text-blue-500 hover:text-blue-700 mr-3">수정</Link>
                                <button type="button"
                                    onClick={() => { if (confirm("특약을 삭제하시겠습니까?")) deleteRider(r.id).then(load); }}
                                    className="text-xs text-red-400 hover:text-red-600">삭제</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
