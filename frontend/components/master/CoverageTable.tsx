'use client';

import Link from "next/link";

interface Props {
    list: any[];
    onDelete: (id: string) => void;
}

export default function CoverageTable({ list, onDelete }: Props) {
    return (
        <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
            <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                <tr>{["담보 종류", "담보명", "한도", "의무", "면책", ""].map(h => <th key={h} className="px-4 py-3 text-left">{h}</th>)}</tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
                {list.length === 0 && <tr><td colSpan={6} className="px-4 py-8 text-center text-gray-400">등록된 담보가 없습니다</td></tr>}
                {list.map((c: any) => (
                    <tr key={c.id} className="hover:bg-gray-50">
                        <td className="px-4 py-3 font-medium">{c.coverageTypeDisplayName}</td>
                        <td className="px-4 py-3 text-gray-600">
                            <Link href={`/employee/master/coverage/${c.id}`} className="hover:text-blue-600 hover:underline">{c.name}</Link>
                        </td>
                        <td className="px-4 py-3">
                            {c.limitOptions?.length > 0 ? `옵션 ${c.limitOptions.length}개` : c.limitType === "UNLIMITED" ? "무한" : c.limitType === "VEHICLE_VALUE" ? "차량가액" : `${c.limitAmount?.toLocaleString()}원`}
                        </td>
                        <td className="px-4 py-3">{c.mandatory ? <span className="px-2 py-0.5 bg-blue-100 text-blue-700 rounded text-xs">의무</span> : "-"}</td>
                        <td className="px-4 py-3 text-gray-500">{c.exclusions?.length ?? 0}개</td>
                        <td className="px-4 py-3 flex gap-3 justify-end">
                            <Link href={`/employee/master/coverage/${c.id}/edit`} className="text-xs text-gray-400 hover:text-blue-600">수정</Link>
                            <button type="button" onClick={() => { if (confirm("삭제?")) onDelete(c.id); }} className="text-xs text-red-400 hover:text-red-600">삭제</button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}
