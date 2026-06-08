'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getExclusions, deleteExclusion } from "@/queries/master";

export default function ExclusionListPage() {
    const [list, setList] = useState<any[]>([]);
    const [error, setError] = useState("");

    const load = async () => { try { setList(await getExclusions()); } catch { setError("목록 로드 실패"); } };
    useEffect(() => { load(); }, []);

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-xl font-bold text-gray-800">공통 면책사유</h1>
                    <p className="text-sm text-gray-500">법령상 공통 면책사유 목록입니다.</p>
                </div>
                <Link href="/employee/master/exclusion/new"
                    className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    + 면책사유 등록
                </Link>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
                <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                    <tr>{["유형", "면책 사유명", "서브항목", "설명", ""].map(h => <th key={h} className="px-4 py-3 text-left">{h}</th>)}</tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                    {list.length === 0 && <tr><td colSpan={5} className="px-4 py-8 text-center text-gray-400">등록된 면책사유가 없습니다</td></tr>}
                    {list.map((e: any) => (
                        <tr key={e.id} className="hover:bg-gray-50">
                            <td className="px-4 py-3 font-medium">{e.exclusionTypeDisplayName}</td>
                            <td className="px-4 py-3 text-gray-700">
                                <Link href={`/employee/master/exclusion/${e.id}`} className="hover:text-blue-600">{e.name}</Link>
                            </td>
                            <td className="px-4 py-3 text-gray-500 text-xs">{e.subItems?.length > 0 ? `${e.subItems.length}개` : "-"}</td>
                            <td className="px-4 py-3 text-gray-500 text-xs max-w-xs truncate">{e.description ?? "-"}</td>
                            <td className="px-4 py-3 text-right">
                                <button type="button" onClick={() => { if (confirm("삭제?")) deleteExclusion(e.id).then(load); }} className="text-xs text-red-400 hover:text-red-600">삭제</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
