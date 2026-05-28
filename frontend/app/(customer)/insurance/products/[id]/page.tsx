'use client';
import { useQuery } from '@tanstack/react-query';
import { getProduct } from '@/queries/products';
import Link from 'next/link';
import { use } from 'react';
import type { Product } from '@/types';

export default function ProductDetailPage({ params }: { params: Promise<{ id: string }> }) {
    const { id } = use(params);
    const { data: product, isLoading, error } = useQuery<Product>({
        queryKey: ['product', id],
        queryFn: () => getProduct(id),
    });

    if (isLoading) return <Shell><p className="text-center py-20 text-slate-500">불러오는 중...</p></Shell>;
    if (error || !product) return <Shell><p className="text-center py-20 text-red-500">상품을 불러올 수 없습니다.</p></Shell>;

    return (
        <Shell>
            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 flex flex-col gap-6">
                <div className="flex items-start gap-3 flex-wrap">
                    <span className="text-sm font-semibold text-blue-600 bg-blue-50 px-3 py-1 rounded-full">{product.target}</span>
                    <span className="text-sm font-semibold text-green-700 bg-green-50 px-3 py-1 rounded-full">{product.status}</span>
                </div>

                <div>
                    <h1 className="text-2xl font-extrabold text-slate-900 break-keep">{product.productName}</h1>
                    <p className="text-sm text-slate-400 mt-1">{product.productCode}</p>
                </div>

                <p className="text-slate-600 leading-relaxed">{product.description}</p>

                <div className="bg-slate-50 rounded-xl p-4 text-sm text-slate-500">
                    판매 기간: {product.saleStartDate} ~ {product.saleEndDate}
                </div>

                <Link
                    href={`/insurance/apply?productId=${product.productId}`}
                    className="w-full text-center py-4 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-xl text-base transition"
                >
                    이 상품으로 가입하기
                </Link>
            </div>
        </Shell>
    );
}

function Shell({ children }: { children: React.ReactNode }) {
    return (
        <div className="max-w-2xl mx-auto px-4 py-12">
            <Link href="/insurance/products" className="text-sm text-slate-400 hover:text-slate-600 mb-6 inline-block">
                ← 상품 목록으로
            </Link>
            {children}
        </div>
    );
}
