'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getExclusions } from "@/queries/master";
import { EXCLUSION_TYPES } from "@/types/master";
import Link from "next/link";

const label = (list: { value: string; label: string }[], val: string) =>
    list.find(t => t.value === val)?.label ?? val;

const TYPE_COLOR: Record<string, string> = {
    INTENTIONAL:  "bg-red-100 text-red-700",
    UNLICENSED:   "bg-orange-100 text-orange-700",
    DRUNK_DRIVING:"bg-amber-100 text-amber-700",
    WAR:          "bg-gray-100 text-gray-600",
    NUCLEAR:      "bg-purple-100 text-purple-700",
    RACING:       "bg-yellow-100 text-yellow-700",
    OTHER:        "bg-gray-100 text-gray-500",
};

export default function ExclusionViewPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const [ex, setEx] = useState<any>(null);
    const [error, setError] = useState("");

    useEffect(() => {
        getExclusions()
            .then((list: any[]) => {
                const found = list.find(e => e.id === Number(id));
                if (found) setEx(found);
                else setError("면책사유를 찾을 수 없습니다.");
            })
            .catch(() => setError("불러오기 실패"));
    }, [id]);

    if (!ex) return <p className="text-sm text-gray-500 p-6">{error || "불러오는 중..."}</p>;

    return (
        <div className="max-w-2xl">
            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <span className={`px-2 py-0.5 rounded text-xs font-medium ${TYPE_COLOR[ex.exclusionType] ?? "bg-gray-100 text-gray-500"}`}>
                    {label(EXCLUSION_TYPES, ex.exclusionType)}
                </span>
                <h1 className="text-xl font-bold text-gray-800">{ex.name}</h1>
            </div>

            <div className="bg-white border border-gray-200 rounded-xl p-6 space-y-5">
                {ex.description && (
                    <div>
                        <p className="text-xs font-medium text-gray-500 mb-1">설명</p>
                        <p className="text-sm text-gray-700 whitespace-pre-wrap leading-relaxed">{ex.description}</p>
                    </div>
                )}

                {ex.subItems?.length > 0 && (
                    <div>
                        <p className="text-xs font-medium text-gray-500 mb-2">세부 항목</p>
                        <ul className="space-y-2 pl-3 border-l-2 border-gray-200">
                            {ex.subItems.map((s: any) => (
                                <li key={s.id} className="flex items-start gap-2 text-sm text-gray-700">
                                    {s.label && <span className="font-semibold shrink-0">{s.label}.</span>}
                                    <span className="flex-1 leading-relaxed">{s.content}</span>
                                    {s.isException && (
                                        <span className="px-1.5 py-0.5 bg-green-100 text-green-700 rounded text-xs shrink-0">예외</span>
                                    )}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                {ex.subItems?.length === 0 && !ex.description && (
                    <p className="text-sm text-gray-400">등록된 세부 내용이 없습니다.</p>
                )}
            </div>
        </div>
    );
}
