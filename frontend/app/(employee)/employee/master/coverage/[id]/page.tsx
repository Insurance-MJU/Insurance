'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getCoverage, getProvisionItems } from "@/queries/master";
import { COVERAGE_TYPES, LIMIT_TYPES, DEDUCTIBLE_TYPES, LIMIT_UNITS, COMPENSATION_TYPES, LIMIT_DETAIL_TYPES } from "@/types/master";
import Link from "next/link";

const label = (list: {value:string;label:string}[], val: string) => list.find(t => t.value === val)?.label ?? val;

function ProvisionLink({ id }: { id: number }) {
    const [item, setItem] = useState<any>(null);
    useEffect(() => {
        getProvisionItems().then((list: any[]) => {
            setItem(list.find((p: any) => p.id === id) ?? null);
        }).catch(() => {});
    }, [id]);
    if (!item) return <span className="text-gray-400 text-xs">불러오는 중...</span>;
    const label = [item.label, item.title].filter(Boolean).join(" ");
    return (
        <Link href={`/employee/master/provisions/${item.provisionId}`}
            className="text-blue-600 hover:underline text-sm">
            {label || `항목 #${id}`}
        </Link>
    );
}
const Row = ({ title, children }: { title: string; children: React.ReactNode }) => (
    <div className="flex gap-4 py-2 border-b border-gray-100 last:border-0">
        <dt className="w-36 shrink-0 text-xs font-medium text-gray-500 pt-0.5">{title}</dt>
        <dd className="flex-1 text-sm text-gray-800">{children}</dd>
    </div>
);

export default function CoverageViewPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const [c, setC] = useState<any>(null);
    const [error, setError] = useState("");

    useEffect(() => {
        getCoverage(id).then(setC).catch(() => setError("담보를 불러올 수 없습니다."));
    }, [id]);

    if (!c) return <p className="text-sm text-gray-500 p-6">{error || "불러오는 중..."}</p>;

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                    <h1 className="text-xl font-bold text-gray-800">{c.name}</h1>
                    <span className="text-xs text-gray-400">ID: {id}</span>
                </div>
                <Link href={`/employee/master/coverage/${id}/edit`}
                    className="px-4 py-1.5 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">수정</Link>
            </div>

            <div className="bg-white border border-gray-200 rounded-xl p-6 space-y-6">
                <dl>
                    <Row title="담보 종류">{label(COVERAGE_TYPES, c.coverageType)}</Row>
                    <Row title="담보명">{c.name}</Row>
                    <Row title="보장 내용">
                        <p className="whitespace-pre-wrap leading-relaxed">{c.description || "-"}</p>
                    </Row>
                    <Row title="한도 유형">{label(LIMIT_TYPES, c.limitType)}</Row>
                    {c.limitAmount != null && <Row title={c.mandatory ? "법정기준한도" : "기본 가입한도"}>{c.limitAmount.toLocaleString()} 원</Row>}
                    <Row title="한도 단위">{label(LIMIT_UNITS, c.limitUnit)}</Row>
                    <Row title="보상 방식">{label(COMPENSATION_TYPES, c.compensationType)}</Row>
                    {c.provisionId != null && <Row title="근거 약관 조항"><ProvisionLink id={c.provisionId} /></Row>}
                    <Row title="자기부담금">
                        {label(DEDUCTIBLE_TYPES, c.deductibleType)}
                        {c.deductibleAmount != null && ` · ${c.deductibleAmount.toLocaleString()} 원`}
                        {c.deductibleRate != null && ` · ${c.deductibleRate * 100}%`}
                    </Row>
                    <Row title="옵션">
                        <span className="flex flex-wrap gap-2">
                            {c.mandatory && <span className="px-2 py-0.5 bg-blue-100 text-blue-700 rounded text-xs">의무</span>}
                            {c.autoRestoration && <span className="px-2 py-0.5 bg-green-100 text-green-700 rounded text-xs">자동 복원</span>}
                            {c.excessPay && <span className="px-2 py-0.5 bg-yellow-100 text-yellow-700 rounded text-xs">초과 비용 지급</span>}
                        </span>
                    </Row>
                </dl>

                {c.limitOptions?.length > 0 && (
                    <section>
                        <h2 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">가입금액 옵션</h2>
                        <div className="space-y-2">
                            {c.limitOptions.map((opt: any) => (
                                <div key={opt.id} className="border border-gray-200 rounded-lg p-3">
                                    <p className="text-sm font-medium text-gray-700 mb-1">{opt.optionName}{opt.isDefault && <span className="ml-2 text-xs text-blue-500">기본값</span>}</p>
                                    <div className="flex flex-wrap gap-3">
                                        {opt.details?.map((d: any) => (
                                            <span key={d.id} className="text-xs text-gray-600">
                                                {label(LIMIT_DETAIL_TYPES, d.detailType)}: {d.amount != null ? `${d.amount.toLocaleString()} 원` : "무한"}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>
                )}

                {c.exclusions?.length > 0 && (
                    <section>
                        <h2 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">면책사항</h2>
                        <div className="space-y-2">
                            {c.exclusions.map((ex: any) => (
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

                {c.requiredCoverages?.length > 0 && (
                    <section>
                        <h2 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-2">선행 가입 필수 담보</h2>
                        <div className="flex flex-wrap gap-2">
                            {c.requiredCoverages.map((r: any) => (
                                <span key={r} className="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs">{label(COVERAGE_TYPES, r)}</span>
                            ))}
                        </div>
                    </section>
                )}
            </div>
        </div>
    );
}
