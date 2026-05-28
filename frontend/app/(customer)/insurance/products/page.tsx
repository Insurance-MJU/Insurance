'use client';
import { useQuery } from '@tanstack/react-query';
import { getProducts } from '@/queries/products';
import Link from 'next/link';
import type { Product } from '@/types';

export default function ProductsPage() {
    const { data: products, isLoading, error } = useQuery<Product[]>({
        queryKey: ['products', 'onSale'],
        queryFn: () => getProducts(true),
    });

    if (isLoading) return <PageShell><p className="text-center py-20 text-slate-500">상품을 불러오는 중...</p></PageShell>;
    if (error) return <PageShell><p className="text-center py-20 text-red-500">상품을 불러올 수 없습니다.</p></PageShell>;

    return (
        <PageShell>
            {products && products.length === 0 ? (
                <p className="text-center py-20 text-slate-500">현재 판매 중인 상품이 없습니다.</p>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {products?.map(product => (
                        <ProductCard key={product.productId} product={product} />
                    ))}
                </div>
            )}
        </PageShell>
    );
}

function PageShell({ children }: { children: React.ReactNode }) {
    return (
        <div className="max-w-6xl mx-auto px-4 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">자동차보험 상품</h1>
            <p className="text-slate-500 mb-10">나에게 맞는 상품을 선택하세요</p>
            {children}
        </div>
    );
}

function ProductCard({ product }: { product: Product }) {
    return (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md hover:border-blue-100 transition-all p-6 flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
                <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-blue-600 bg-blue-50 px-2 py-0.5 rounded-full shrink-0">{product.target}</span>
                    <span className="text-xs font-semibold text-green-700 bg-green-50 px-2 py-0.5 rounded-full shrink-0">{product.status}</span>
                </div>
                <h2 className="text-lg font-bold text-slate-900 break-keep">{product.productName}</h2>
                <p className="text-xs text-slate-400">{product.productCode}</p>
            </div>
            <p className="text-sm text-slate-600 leading-relaxed flex-1">{product.description}</p>
            <div className="text-xs text-slate-400">
                판매기간: {product.saleStartDate} ~ {product.saleEndDate}
            </div>
            <div className="flex gap-2">
                <Link
                    href={`/insurance/products/${product.productId}`}
                    className="flex-1 text-center py-2.5 bg-white hover:bg-slate-50 text-slate-700 border border-gray-200 font-semibold rounded-xl text-sm transition"
                >
                    상세보기
                </Link>
                <Link
                    href={`/insurance/apply?productId=${product.productId}`}
                    className="flex-1 text-center py-2.5 bg-slate-900 hover:bg-slate-800 text-white font-semibold rounded-xl text-sm transition"
                >
                    가입하기
                </Link>
            </div>
        </div>
    );
}
