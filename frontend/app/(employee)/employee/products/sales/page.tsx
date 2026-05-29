'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getProducts, changeProductStatus } from "@/queries/products";

const STAGES = ["APPROVED", "SALE_PENDING", "ON_SALE", "DISCONTINUED",
                "FSS_APPROVED", "FILING", "FILED"] as const;

const STATUS_META: Record<string, { label: string; color: string; bg: string }> = {
    APPROVED:     { label: "인가 완료",    color: "text-indigo-600", bg: "bg-indigo-100" },
    SALE_PENDING: { label: "판매신청 중",  color: "text-purple-600", bg: "bg-purple-100" },
    ON_SALE:      { label: "판매 중",      color: "text-green-700",  bg: "bg-green-100"  },
    DISCONTINUED: { label: "판매 중단",    color: "text-red-600",    bg: "bg-red-100"    },
    // 호환
    FSS_APPROVED: { label: "금감원 인가완료", color: "text-indigo-600", bg: "bg-indigo-100" },
    FILING:       { label: "판매신고 중",     color: "text-purple-600", bg: "bg-purple-100" },
    FILED:        { label: "판매 확정",       color: "text-teal-700",   bg: "bg-teal-100"   },
};

const NEXT_ACTION: Record<string, { label: string; next: string; style: string }> = {
    APPROVED:     { label: "판매 신청 →", next: "SALE_PENDING", style: "bg-purple-600 text-white hover:bg-purple-700" },
    SALE_PENDING: { label: "판매 개시 →", next: "ON_SALE",      style: "bg-green-600 text-white hover:bg-green-700"  },
    ON_SALE:      { label: "판매 중단",   next: "DISCONTINUED", style: "bg-red-50 text-red-600 border border-red-200 hover:bg-red-100" },
    // 호환
    FSS_APPROVED: { label: "판매 신청 →", next: "SALE_PENDING", style: "bg-purple-600 text-white hover:bg-purple-700" },
    FILING:       { label: "판매 확정 →", next: "ON_SALE",      style: "bg-green-600 text-white hover:bg-green-700"  },
    FILED:        { label: "판매 시작 →", next: "ON_SALE",      style: "bg-green-600 text-white hover:bg-green-700"  },
};

export default function SalesPage() {
    const [products, setProducts] = useState<any[]>([]);
    const [loading, setLoading]   = useState<number | null>(null);

    const load = async () => {
        const all = await getProducts();
        setProducts(all.filter((p: any) => STAGES.includes(p.status)));
    };

    useEffect(() => { load(); }, []);

    const handleTransition = async (id: number, name: string, next: string, label: string) => {
        if (!confirm(`"${name}"\n${label} 처리할까요?`)) return;
        setLoading(id);
        try { await changeProductStatus(id, next); await load(); }
        catch (e: any) { alert(e?.message ?? "상태 변경 실패"); }
        finally { setLoading(null); }
    };

    const onSale   = products.filter(p => p.status === "ON_SALE");
    const inProgress = products.filter(p => p.status !== "ON_SALE" && p.status !== "DISCONTINUED");
    const discontinued = products.filter(p => p.status === "DISCONTINUED");

    const ProductCard = ({ p }: { p: any }) => {
        const meta   = STATUS_META[p.status as keyof typeof STATUS_META];
        const action = NEXT_ACTION[p.status];
        return (
            <div className="bg-white border border-gray-200 rounded-xl p-5 flex items-center justify-between gap-4">
                <div className="min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                        <span className={`text-[10px] px-2 py-0.5 rounded-full font-medium ${meta.bg} ${meta.color}`}>
                            {meta.label}
                        </span>
                        <span className="text-xs text-gray-400 font-mono">{p.productCode}</span>
                    </div>
                    <p className="font-medium text-gray-800">{p.productName}</p>
                    <p className="text-xs text-gray-400 mt-0.5">
                        {p.lineOfBusinessDisplayName}
                        {p.saleStartDate && ` · 판매시작 ${p.saleStartDate}`}
                    </p>
                </div>
                <div className="flex items-center gap-2 shrink-0">
                    {action && (
                        <button
                            disabled={loading === p.id}
                            onClick={() => handleTransition(p.id, p.productName, action.next, action.label)}
                            className={`px-3 py-1.5 text-xs rounded-lg font-medium disabled:opacity-40 ${action.style}`}>
                            {loading === p.id ? "처리 중..." : action.label}
                        </button>
                    )}
                    <Link href={`/employee/products/${p.id}`}
                        className="text-xs text-gray-400 hover:text-gray-600">
                        상세
                    </Link>
                </div>
            </div>
        );
    };

    return (
        <div className="max-w-4xl space-y-6">
            <div>
                <h1 className="text-xl font-bold text-gray-800">판매신청</h1>
                <p className="text-sm text-gray-500 mt-0.5">판매 신고 · 확정 · 개시 · 중단 관리</p>
            </div>

            {/* 신청 진행 중 */}
            <section className="space-y-3">
                <h2 className="text-sm font-semibold text-gray-500">신청 진행 중</h2>
                {inProgress.length === 0 ? (
                    <div className="bg-white border border-gray-200 rounded-xl p-8 text-center text-gray-400 text-sm">
                        진행 중인 항목 없음
                    </div>
                ) : (
                    inProgress.map(p => <ProductCard key={p.id} p={p} />)
                )}
            </section>

            {/* 판매 중 */}
            {onSale.length > 0 && (
                <section className="space-y-3">
                    <h2 className="text-sm font-semibold text-gray-500">판매 중 ({onSale.length})</h2>
                    {onSale.map(p => <ProductCard key={p.id} p={p} />)}
                </section>
            )}

            {/* 판매 중단 */}
            {discontinued.length > 0 && (
                <section className="space-y-3">
                    <h2 className="text-sm font-semibold text-gray-500 text-red-400">판매 중단 ({discontinued.length})</h2>
                    {discontinued.map(p => <ProductCard key={p.id} p={p} />)}
                </section>
            )}
        </div>
    );
}
