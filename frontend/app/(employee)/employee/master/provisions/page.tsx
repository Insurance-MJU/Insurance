'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getProvisions, deleteProvision } from "@/queries/master";

export default function ProvisionsListPage() {
    const [list, setList] = useState<any[]>([]);
    const [error, setError] = useState("");

    const load = async () => {
        try { setList(await getProvisions()); }
        catch { setError("목록 로드 실패"); }
    };
    useEffect(() => { load(); }, []);

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-xl font-bold text-gray-800">표준약관</h1>
                    <p className="text-sm text-gray-500">편·장·절·조 계층 구조로 관리합니다.</p>
                </div>
                <Link href="/employee/master/provisions/new"
                    className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    + 약관 등록
                </Link>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <div className="space-y-2">
                {list.length === 0 && (
                    <p className="text-sm text-gray-400 py-8 text-center">등록된 약관이 없습니다</p>
                )}
                {list.map((p: any) => (
                    <div key={p.id}
                        className="flex items-center justify-between bg-white border border-gray-200 rounded-xl px-5 py-4 hover:border-blue-300 transition-colors">
                        <Link href={`/employee/master/provisions/${p.id}`}
                            className="flex-1 text-sm font-medium text-gray-800 hover:text-blue-600">
                            {p.title}
                        </Link>
                        <div className="flex items-center gap-3">
                            <Link href={`/employee/master/provisions/${p.id}`}
                                className="text-xs text-blue-500 hover:underline">
                                조항 보기
                            </Link>
                            <button type="button"
                                onClick={() => { if (confirm("약관을 삭제하시겠습니까? (모든 조항 포함)")) deleteProvision(p.id).then(load); }}
                                className="text-xs text-red-400 hover:text-red-600">
                                삭제
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
