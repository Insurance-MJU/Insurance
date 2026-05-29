'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getProducts, changeProductStatus } from "@/queries/products";

const STAGES = ["DESIGN", "DESIGN_COMPLETE"] as const;

const STATUS_META = {
    DESIGN:          { label: "설계 중",    color: "text-gray-600",   bg: "bg-gray-100",   step: 1 },
    DESIGN_COMPLETE: { label: "설계 완료",  color: "text-yellow-700", bg: "bg-yellow-100", step: 2 },
    // 호환
    DESIGNING:      { label: "설계 중",    color: "text-gray-600",   bg: "bg-gray-100",   step: 1 },
    KIDI_SUBMITTED: { label: "보험개발원 제출", color: "text-orange-600", bg: "bg-orange-100", step: 2 },
    KIDI_CONFIRMED: { label: "요율확인서 수령", color: "text-yellow-700", bg: "bg-yellow-100", step: 3 },
} as const;

export default function RateCheckPage() {
    const [products, setProducts] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);

    const load = async () => {
        const all = await getProducts();
        setProducts(all.filter((p: any) => STAGES.includes(p.status)));
    };

    useEffect(() => { load(); }, []);

    const handleSubmitKidi = async (id: number, name: string) => {
        if (!confirm(`"${name}"의 설계를 완료 처리할까요?`)) return;
        setLoading(true);
        try { await changeProductStatus(id, "DESIGN_COMPLETE"); await load(); }
        catch (e: any) { alert(e?.message ?? "상태 변경 실패"); }
        finally { setLoading(false); }
    };

    return (
        <div className="max-w-4xl space-y-5">
            <div>
                <h1 className="text-xl font-bold text-gray-800">요율확인</h1>
                <p className="text-sm text-gray-500 mt-0.5">
                    보험개발원(KIDI) 제출 → 요율확인서 수령까지의 진행 현황
                </p>
            </div>

            {/* 단계 안내 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <div className="flex items-center gap-0">
                    {[
                        { step: 1, label: "설계 완료", sub: "상품 설계 완료 후 제출 대기" },
                        { step: 2, label: "보험개발원 제출", sub: "요율확인서 발급 대기 중" },
                        { step: 3, label: "요율확인서 수령", sub: "인가신청 단계로 이동 가능" },
                    ].map((s, i) => (
                        <div key={s.step} className="flex items-center">
                            <div className="flex flex-col items-center w-36 px-2">
                                <div className="w-8 h-8 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center text-xs font-bold">{s.step}</div>
                                <p className="text-xs font-medium text-gray-700 mt-1.5 text-center">{s.label}</p>
                                <p className="text-[10px] text-gray-400 text-center mt-0.5">{s.sub}</p>
                            </div>
                            {i < 2 && <div className="h-0.5 w-6 bg-gray-200 mb-6" />}
                        </div>
                    ))}
                </div>
            </div>

            {/* 상품 목록 */}
            {products.length === 0 ? (
                <div className="bg-white border border-gray-200 rounded-xl p-10 text-center text-gray-400 text-sm">
                    요율확인 진행 중인 상품이 없습니다
                </div>
            ) : (
                <div className="space-y-3">
                    {products.map((p: any) => {
                        const meta = STATUS_META[p.status as keyof typeof STATUS_META];
                        return (
                            <div key={p.id} className="bg-white border border-gray-200 rounded-xl p-5 flex items-center justify-between gap-4">
                                <div className="min-w-0">
                                    <div className="flex items-center gap-2 mb-1">
                                        <span className={`text-[10px] px-2 py-0.5 rounded-full font-medium ${meta.bg} ${meta.color}`}>
                                            {meta.label}
                                        </span>
                                        <span className="text-xs text-gray-400 font-mono">{p.productCode}</span>
                                    </div>
                                    <p className="font-medium text-gray-800">{p.productName}</p>
                                    <p className="text-xs text-gray-400 mt-0.5">{p.lineOfBusinessDisplayName}</p>
                                </div>

                                <div className="flex items-center gap-2 shrink-0">
                                    {p.status === "DESIGNING" && (
                                        <button
                                            disabled={loading}
                                            onClick={() => handleSubmitKidi(p.id, p.productName)}
                                            className="px-3 py-1.5 text-xs bg-orange-500 text-white rounded-lg hover:bg-orange-600 disabled:opacity-40">
                                            보험개발원 제출 →
                                        </button>
                                    )}
                                    {p.status === "KIDI_SUBMITTED" && (
                                        <span className="text-xs text-orange-500 bg-orange-50 px-3 py-1.5 rounded-lg">
                                            요율확인서 대기 중
                                        </span>
                                    )}
                                    {p.status === "KIDI_CONFIRMED" && (
                                        <span className="text-xs text-yellow-700 bg-yellow-50 px-3 py-1.5 rounded-lg">
                                            인가신청 가능
                                        </span>
                                    )}
                                    <Link href={`/employee/products/${p.id}/approval`}
                                        className="px-3 py-1.5 text-xs border border-gray-300 rounded-lg hover:bg-gray-50">
                                        서류 관리
                                    </Link>
                                    <Link href={`/employee/products/${p.id}`}
                                        className="text-xs text-gray-400 hover:text-gray-600">
                                        상세
                                    </Link>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
