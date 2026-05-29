'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { getProduct, deleteProduct } from "@/queries/products";
import ProductBasicInfo    from "../_components/ProductBasicInfo";
import ProductCoverages    from "../_components/ProductCoverages";
import ProductRiders       from "../_components/ProductRiders";


const STATUS_META: Record<string, { label: string; color: string; bg: string }> = {
    DESIGN:           { label: "설계 중",      color: "text-gray-600",   bg: "bg-gray-100" },
    DESIGN_COMPLETE:  { label: "설계 완료",    color: "text-yellow-700", bg: "bg-yellow-100" },
    APPROVAL_PENDING: { label: "인가신청 중",  color: "text-blue-600",   bg: "bg-blue-100" },
    APPROVED:         { label: "인가 완료",    color: "text-indigo-600", bg: "bg-indigo-100" },
    SALE_PENDING:     { label: "판매신청 중",  color: "text-purple-600", bg: "bg-purple-100" },
    ON_SALE:          { label: "판매 중",      color: "text-green-700",  bg: "bg-green-100" },
    SALE_EXPIRED:     { label: "판매기간 만료",color: "text-gray-500",   bg: "bg-gray-100" },
    DISCONTINUED:     { label: "판매 중단",    color: "text-red-600",    bg: "bg-red-100" },
    DESIGNING:      { label: "설계 중",        color: "text-gray-600",   bg: "bg-gray-100" },
    KIDI_SUBMITTED: { label: "보험개발원 제출", color: "text-orange-600", bg: "bg-orange-100" },
    KIDI_CONFIRMED: { label: "요율확인서 수령", color: "text-yellow-700", bg: "bg-yellow-100" },
    FSS_APPLIED:    { label: "금감원 인가신청", color: "text-blue-600",   bg: "bg-blue-100" },
    FSS_APPROVED:   { label: "금감원 인가완료", color: "text-indigo-600", bg: "bg-indigo-100" },
    FILING:         { label: "판매신고 중",     color: "text-purple-600", bg: "bg-purple-100" },
    FILED:          { label: "판매 확정",       color: "text-teal-700",   bg: "bg-teal-100" },
};

export default function ProductDetailPage() {
    const { id } = useParams();
    const router  = useRouter();
    const pid     = String(id);

    const [product, setProduct] = useState<any>(null);
    const [error, setError]     = useState("");

    useEffect(() => {
        if (!pid) return;
        getProduct(pid as any)
            .then(res => setProduct(res.data ?? res))
            .catch(() => setError("상품 정보 로드 실패"));
    }, [pid]);

    if (error)    return <p className="text-red-500 text-sm p-6">{error}</p>;
    if (!product) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;

    const sm = STATUS_META[product.status] ?? { label: product.status, color: "text-gray-600", bg: "bg-gray-100" };

    const handleDelete = async () => {
        if (!confirm(`"${product.productName}" 상품을 삭제할까요?`)) return;
        await deleteProduct(pid as any);
        router.push("/employee/products");
    };

    return (
        <div className="max-w-3xl space-y-5">
            <div className="flex items-start justify-between">
                <div>
                    <div className="flex items-center gap-3 mb-1">
                        <Link href="/employee/products" className="text-sm text-gray-400 hover:text-gray-600">← 목록</Link>
                        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${sm.bg} ${sm.color}`}>{sm.label}</span>
                    </div>
                    <h1 className="text-xl font-bold text-gray-800">{product.productName}</h1>
                    <p className="text-sm text-gray-400 font-mono mt-0.5">{product.productCode}</p>
                </div>
                <div className="flex gap-2">
                    <Link href={`/employee/products/${pid}/approval`}
                        className="px-3 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                        인가 관리 →
                    </Link>
                    <Link href={`/employee/products/${pid}/edit`}
                        className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg hover:bg-gray-50">
                        수정
                    </Link>
                    <button onClick={handleDelete}
                        className="px-3 py-1.5 text-sm border border-red-200 text-red-500 rounded-lg hover:bg-red-50">
                        삭제
                    </button>
                </div>
            </div>

            <ProductBasicInfo   product={product} />
            <ProductCoverages   coverages={product.coverages} />
            <ProductRiders      riders={product.riders} />
        </div>
    );
}
