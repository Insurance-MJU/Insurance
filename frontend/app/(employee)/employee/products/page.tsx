'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getProducts, deleteProduct } from "@/queries/products";

const STATUS_LABELS: Record<string, { label: string; color: string }> = {
    DESIGN:           { label: "설계 중",      color: "bg-gray-100 text-gray-600" },
    DESIGN_COMPLETE:  { label: "설계 완료",    color: "bg-yellow-100 text-yellow-700" },
    APPROVAL_PENDING: { label: "인가신청 중",  color: "bg-blue-100 text-blue-600" },
    APPROVED:         { label: "인가 완료",    color: "bg-indigo-100 text-indigo-600" },
    SALE_PENDING:     { label: "판매신청 중",  color: "bg-purple-100 text-purple-600" },
    ON_SALE:          { label: "판매 중",      color: "bg-green-100 text-green-700" },
    SALE_EXPIRED:     { label: "판매기간 만료",color: "bg-gray-100 text-gray-500" },
    DISCONTINUED:     { label: "판매 중단",    color: "bg-red-100 text-red-600" },
    // toss-app 호환
    DESIGNING:      { label: "설계 중",           color: "bg-gray-100 text-gray-600" },
    KIDI_SUBMITTED: { label: "보험개발원 제출",    color: "bg-orange-100 text-orange-600" },
    KIDI_CONFIRMED: { label: "요율확인서 수령",    color: "bg-yellow-100 text-yellow-700" },
    FSS_APPLIED:    { label: "금감원 인가신청",    color: "bg-blue-100 text-blue-600" },
    FSS_APPROVED:   { label: "금감원 인가완료",    color: "bg-indigo-100 text-indigo-600" },
    FILING:         { label: "판매신고 중",        color: "bg-purple-100 text-purple-600" },
    FILED:          { label: "판매 확정",          color: "bg-teal-100 text-teal-700" },
};

const LOB_LABELS: Record<string, string> = {
    PERSONAL_AUTO: "개인용자동차보험",
    COMMERCIAL_AUTO: "업무용자동차보험",
    BUSINESS_AUTO: "영업용자동차보험",
    MOTORCYCLE: "이륜자동차보험",
    AGRICULTURAL: "농기계보험",
};

export default function ProductListPage() {
    const [products, setProducts] = useState<any[]>([]);
    const [error, setError] = useState("");

    const load = async () => {
        try { setProducts(await getProducts()); }
        catch { setError("상품 목록 로드 실패"); }
    };
    useEffect(() => { load(); }, []);

    return (
        <div className="max-w-5xl">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-xl font-bold text-gray-800">보험상품 관리</h1>
                    <p className="text-sm text-gray-500">상품 목록 및 신규 등록</p>
                </div>
                <Link href="/employee/products/new"
                    className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    + 신규 상품 등록
                </Link>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
                <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                    <tr>
                        {["상품코드", "상품명", "종목", "판매기간", "상태", "담보수", "특약수", ""].map(h => (
                            <th key={h} className="px-4 py-3 text-left">{h}</th>
                        ))}
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                    {products.length === 0 && (
                        <tr><td colSpan={8} className="px-4 py-8 text-center text-gray-400">등록된 상품이 없습니다</td></tr>
                    )}
                    {products.map((p: any) => {
                        const st = STATUS_LABELS[p.status] ?? { label: p.statusDisplayName ?? p.status, color: "bg-gray-100 text-gray-600" };
                        return (
                            <tr key={p.id} className="hover:bg-gray-50">
                                <td className="px-4 py-3 font-mono text-xs text-gray-500">{p.productCode}</td>
                                <td className="px-4 py-3 font-medium">
                                    <Link href={`/employee/products/${p.id}`} className="hover:text-blue-600">{p.productName}</Link>
                                </td>
                                <td className="px-4 py-3 text-gray-500 text-xs">{p.lineOfBusinessDisplayName ?? LOB_LABELS[p.lineOfBusiness] ?? p.lineOfBusiness}</td>
                                <td className="px-4 py-3 text-xs text-gray-500">
                                    {p.saleStartDate} ~ {p.saleEndDate ?? "무기한"}
                                </td>
                                <td className="px-4 py-3">
                                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${st.color}`}>{st.label}</span>
                                </td>
                                <td className="px-4 py-3 text-center text-gray-500">{p.coverages?.length ?? 0}</td>
                                <td className="px-4 py-3 text-center text-gray-500">{p.riders?.length ?? 0}</td>
                                <td className="px-4 py-3 text-right">
                                    <div className="flex gap-2 justify-end">
                                        <Link href={`/employee/products/${p.id}/edit`}
                                            className="text-xs text-blue-500 hover:text-blue-700">수정</Link>
                                        <button onClick={() => { if (confirm("삭제?")) deleteProduct(p.id).then(load); }}
                                            className="text-xs text-red-400 hover:text-red-600">삭제</button>
                                    </div>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}
