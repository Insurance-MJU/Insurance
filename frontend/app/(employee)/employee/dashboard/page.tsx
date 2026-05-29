'use client';

import { useEffect, useState } from "react";
import { getProducts } from "@/queries/products";

export default function DashboardPage() {
    const [products, setProducts] = useState<any[]>([]);

    useEffect(() => {
        getProducts().then(setProducts).catch(() => { });
    }, []);

    const onSale = products.filter(p => p.status === "ON_SALE").length;
    const inProgress = products.filter(p =>
        ["DESIGN", "DESIGN_COMPLETE", "APPROVAL_PENDING", "APPROVED", "SALE_PENDING"].includes(p.status)
    ).length;
    const designing = products.filter(p =>
        ["SALE_EXPIRED", "DISCONTINUED"].includes(p.status)
    ).length;

    return (
        <div className="max-w-5xl space-y-8">
            <div>
                <h1 className="text-xl font-bold text-gray-800">대시보드</h1>
                <p className="text-sm text-gray-500 mt-0.5">보험사 상품·계약 관리 시스템</p>
            </div>

            {/* 상품 현황 요약 */}
            <div className="grid grid-cols-3 gap-4">
                {[
                    { label: "판매 중", value: onSale, color: "text-green-600", sub: "현재 판매 중인 상품" },
                    { label: "인허가 진행", value: inProgress, color: "text-blue-600", sub: "심사·신고 진행 중" },
                    { label: "판매종료/중지", value: designing, color: "text-gray-600", sub: "판매기간 만료·중지 상품" },
                ].map(s => (
                    <div key={s.label} className="bg-white border border-gray-200 rounded-xl p-5">
                        <p className="text-xs text-gray-400 mb-1">{s.label}</p>
                        <p className={`text-3xl font-bold ${s.color}`}>{s.value}</p>
                        <p className="text-xs text-gray-400 mt-1">{s.sub}</p>
                    </div>
                ))}
            </div>

        </div>
    );
}
