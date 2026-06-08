'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { getRider, getProvisionItems } from "@/queries/master";

const TYPE_BADGE: Record<string, string> = {
    DISCOUNT: "bg-green-100 text-green-700",
    ADD_ON:   "bg-blue-100 text-blue-700",
};

const Row = ({ title, children }: { title: string; children: React.ReactNode }) => (
    <div className="flex gap-4 py-2 border-b border-gray-100 last:border-0">
        <dt className="w-36 shrink-0 text-xs font-medium text-gray-500 pt-0.5">{title}</dt>
        <dd className="flex-1 text-sm text-gray-800">{children}</dd>
    </div>
);

function ProvisionLink({ id }: { id: number }) {
    const [item, setItem] = useState<any>(null);
    useEffect(() => {
        getProvisionItems().then((list: any[]) => setItem(list.find(p => p.id === id) ?? null)).catch(() => {});
    }, [id]);
    if (!item) return <span className="text-gray-400 text-xs">불러오는 중...</span>;
    const label = [item.label, item.title].filter(Boolean).join(" ");
    return (
        <Link href={`/employee/master/provisions/${item.provisionId}`} className="text-blue-600 hover:underline text-sm">
            {label || `항목 #${id}`}
        </Link>
    );
}

export default function RiderViewPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const [r, setR] = useState<any>(null);
    const [error, setError] = useState("");

    useEffect(() => {
        getRider(id).then(setR).catch(() => setError("특약을 불러올 수 없습니다."));
    }, [id]);

    if (!r) return <p className="text-sm text-gray-500 p-6">{error || "불러오는 중..."}</p>;

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                    <span className={`px-2 py-0.5 rounded text-xs font-medium ${TYPE_BADGE[r.riderType] ?? "bg-gray-100 text-gray-600"}`}>
                        {r.riderTypeDisplayName}
                    </span>
                    <h1 className="text-xl font-bold text-gray-800">{r.name}</h1>
                    <span className="text-xs text-gray-400">ID: {id}</span>
                </div>
                <Link href={`/employee/master/rider/${id}/edit`}
                    className="px-4 py-1.5 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">수정</Link>
            </div>

            <div className="bg-white border border-gray-200 rounded-xl p-6 space-y-6">
                <dl>
                    <Row title="특약 코드"><span className="font-mono text-xs bg-gray-100 px-2 py-1 rounded">{r.riderCode}</span></Row>
                    <Row title="특약명">{r.name}</Row>
                    <Row title="설명"><p className="whitespace-pre-wrap leading-relaxed">{r.description || "-"}</p></Row>
                    <Row title="할인율">
                        {r.discountRate != null ? `${(r.discountRate * 100).toFixed(2)}%` : "-"}
                    </Row>
                    {r.provisionId != null && <Row title="근거 약관 조항"><ProvisionLink id={r.provisionId} /></Row>}
                    <Row title="옵션">
                        <span className="flex flex-wrap gap-2">
                            {r.mandatory && <span className="px-2 py-0.5 bg-blue-100 text-blue-700 rounded text-xs">의무 부가</span>}
                            {!r.mandatory && <span className="text-gray-400 text-xs">임의 부가</span>}
                        </span>
                    </Row>
                </dl>

                {r.exclusions?.length > 0 && (
                    <section>
                        <h2 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">면책사항</h2>
                        <div className="space-y-2">
                            {r.exclusions.map((ex: any) => (
                                <div key={ex.id} className="border border-gray-200 rounded-lg p-3 bg-gray-50">
                                    <p className="text-sm font-medium text-gray-700">{ex.name}</p>
                                    <p className="text-sm text-gray-600 mt-0.5">{ex.description}</p>
                                    {ex.subItems?.length > 0 && (
                                        <ul className="mt-2 pl-3 space-y-1 border-l-2 border-gray-200">
                                            {ex.subItems.map((s: any) => (
                                                <li key={s.id} className="flex items-start gap-2 text-xs text-gray-600">
                                                    <span className="font-medium shrink-0">{s.label}.</span>
                                                    <span className="flex-1">{s.content}</span>
                                                    {s.isException && <span className="px-1.5 py-0.5 bg-green-100 text-green-700 rounded shrink-0">예외</span>}
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </div>
                            ))}
                        </div>
                    </section>
                )}
            </div>
        </div>
    );
}
