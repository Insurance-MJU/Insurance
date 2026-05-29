'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getProducts, changeProductStatus } from "@/queries/products";

const STAGES = ["DESIGN_COMPLETE", "APPROVAL_PENDING", "APPROVED"] as const;

const STATUS_META: Record<string, { label: string; color: string; bg: string }> = {
    DESIGN_COMPLETE:  { label: "설계 완료",   color: "text-yellow-700", bg: "bg-yellow-100" },
    APPROVAL_PENDING: { label: "인가신청 중", color: "text-blue-600",   bg: "bg-blue-100"   },
    APPROVED:         { label: "인가 완료",   color: "text-indigo-600", bg: "bg-indigo-100" },
    // 호환
    KIDI_CONFIRMED: { label: "요율확인서 수령", color: "text-yellow-700", bg: "bg-yellow-100" },
    FSS_APPLIED:    { label: "금감원 인가신청", color: "text-blue-600",   bg: "bg-blue-100"   },
    FSS_APPROVED:   { label: "금감원 인가완료", color: "text-indigo-600", bg: "bg-indigo-100" },
};

export default function LicensePage() {
    const [products, setProducts] = useState<any[]>([]);
    const [loading, setLoading]   = useState(false);

    const load = async () => {
        const all = await getProducts();
        setProducts(all.filter((p: any) => STAGES.includes(p.status)));
    };

    useEffect(() => { load(); }, []);

    const handleApprove = async (id: number, name: string) => {
        if (!confirm(`"${name}"의 금감원 인가를 완료 처리할까요?`)) return;
        setLoading(true);
        try { await changeProductStatus(id, "APPROVED"); await load(); }
        catch (e: any) { alert(e?.message ?? "상태 변경 실패"); }
        finally { setLoading(false); }
    };

    return (
        <div className="max-w-4xl space-y-5">
            <div>
                <h1 className="text-xl font-bold text-gray-800">인가신청</h1>
                <p className="text-sm text-gray-500 mt-0.5">
                    금감원(FSS) 인가신청 및 인가 결과 처리
                </p>
            </div>

            {/* 단계 안내 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <div className="flex items-center">
                    {[
                        { step: 1, label: "인가신청 준비",   sub: "보험상품신고서 업로드 시 자동 신청" },
                        { step: 2, label: "금감원 심사 중",  sub: "FSS 심사 결과 대기" },
                        { step: 3, label: "인가 완료",       sub: "판매신청 단계로 이동 가능" },
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
                <p className="mt-3 text-[11px] text-blue-500">
                    ℹ 보험상품신고서(FSS_APPLICATION) 업로드 시 '금감원 인가신청' 상태로 자동 전환됩니다.
                </p>
            </div>

            {products.length === 0 ? (
                <div className="bg-white border border-gray-200 rounded-xl p-10 text-center text-gray-400 text-sm">
                    인가신청 진행 중인 상품이 없습니다
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
                                    {(p.status === "DESIGN_COMPLETE" || p.status === "KIDI_CONFIRMED") && (
                                        <Link href={`/employee/products/${p.id}/approval`}
                                            className="px-3 py-1.5 text-xs bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                                            인가신청서 업로드 →
                                        </Link>
                                    )}
                                    {(p.status === "APPROVAL_PENDING" || p.status === "FSS_APPLIED") && (
                                        <>
                                            <span className="text-xs text-blue-500 bg-blue-50 px-3 py-1.5 rounded-lg">
                                                심사 중
                                            </span>
                                            <button
                                                disabled={loading}
                                                onClick={() => handleApprove(p.id, p.productName)}
                                                className="px-3 py-1.5 text-xs bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-40">
                                                인가 완료 처리
                                            </button>
                                        </>
                                    )}
                                    {(p.status === "APPROVED" || p.status === "FSS_APPROVED") && (
                                        <span className="text-xs text-indigo-600 bg-indigo-50 px-3 py-1.5 rounded-lg">
                                            판매신청 가능
                                        </span>
                                    )}
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
