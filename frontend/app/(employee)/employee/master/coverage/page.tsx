'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getCoverages, deleteCoverage } from "@/queries/master";
import CoverageTable from "@/components/master/CoverageTable";

export default function CoverageListPage() {
    const [list, setList] = useState<any[]>([]);
    const [error, setError] = useState("");

    const load = async () => { try { setList(await getCoverages()); } catch { setError("목록 로드 실패"); } };
    useEffect(() => { load(); }, []);

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-xl font-bold text-gray-800">담보</h1>
                    <p className="text-sm text-gray-500">자동차보험 법정 담보 목록입니다.</p>
                </div>
                <Link href="/employee/master/coverage/new"
                    className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    + 담보 등록
                </Link>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <CoverageTable list={list} onDelete={id => deleteCoverage(id).then(load)} />
        </div>
    );
}
