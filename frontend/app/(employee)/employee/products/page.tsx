'use client';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getProducts, applyForApproval, applyRateVerification, confirmSale } from '@/queries/products';
import Link from 'next/link';
import type { Product } from '@/types';

// 시나리오 흐름: CT-04(인가신청) 안에 CT-05(요율검증) include → CT-06(판매확정)
// 백엔드 상태: 설계완료 → 인가신청중 → 인가완료 → 판매신청중 → 판매중
const ACTION_MAP: Record<string, { label: string; color: string; fn: (id: string) => Promise<Product>; hint?: string }> = {
    '설계완료':   { label: '① 인가 신청 (CT-04)', color: 'bg-blue-600 hover:bg-blue-700', fn: applyForApproval,
                    hint: '인가 신청 전 보험개발원 요율검증(CT-05)이 선행되어야 합니다.' },
    '인가완료':   { label: '② 요율검증/판매신청 (CT-05)', color: 'bg-indigo-600 hover:bg-indigo-700', fn: applyRateVerification },
    '판매신청중': { label: '③ 판매 확정 (CT-06)', color: 'bg-green-600 hover:bg-green-700', fn: confirmSale },
};

const STATUS_COLOR: Record<string, string> = {
    '설계중':       'bg-gray-100 text-gray-600',
    '설계완료':     'bg-blue-50 text-blue-700',
    '인가신청중':   'bg-yellow-50 text-yellow-700',
    '인가완료':     'bg-indigo-50 text-indigo-700',
    '판매신청중':   'bg-orange-50 text-orange-700',
    '판매중':       'bg-green-50 text-green-700',
    '판매기간만료': 'bg-red-50 text-red-500',
    '판매중지':     'bg-red-50 text-red-500',
};

export default function EmployeeProductsPage() {
    const qc = useQueryClient();
    const { data: products, isLoading } = useQuery<Product[]>({
        queryKey: ['products', 'all'],
        queryFn: () => getProducts(),
    });

    const mutation = useMutation({
        mutationFn: ({ id, fn }: { id: string; fn: (id: string) => Promise<Product> }) => fn(id),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['products'] }),
        onError: (e: any) => alert(e.message),
    });

    return (
        <div className="max-w-5xl mx-auto px-6 py-12">
            <div className="flex items-center justify-between mb-2">
                <h1 className="text-3xl font-extrabold text-slate-900">상품 관리</h1>
                <Link href="/employee/products/new"
                    className="px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white text-sm font-bold rounded-xl transition">
                    + 신규 상품 등록 (CT-01)
                </Link>
            </div>
            <p className="text-slate-500 mb-10">상품 설계 · 인가 신청 · 요율검증 · 판매 확정을 처리하세요</p>

            {isLoading && <p className="text-center py-20 text-slate-500">불러오는 중...</p>}
            {products?.length === 0 && <p className="text-center py-20 text-slate-400">등록된 상품이 없습니다.</p>}

            <div className="flex flex-col gap-4">
                {products?.map(p => {
                    const action = ACTION_MAP[p.status];
                    return (
                        <div key={p.productId} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex items-center justify-between gap-4">
                            <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${STATUS_COLOR[p.status] ?? 'bg-slate-100 text-slate-600'}`}>
                                        {p.status}
                                    </span>
                                    <span className="text-xs text-slate-400">{p.productCode}</span>
                                </div>
                                <h3 className="font-bold text-slate-900 break-keep">{p.productName}</h3>
                                <p className="text-sm text-slate-500 mt-0.5">
                                    {p.saleStartDate} ~ {p.saleEndDate} · {p.target}
                                </p>
                            </div>
                            {action && (
                                <div className="flex flex-col items-end gap-1">
                                    {action.hint && (
                                        <p className="text-xs text-amber-600 text-right max-w-48">⚠ {action.hint}</p>
                                    )}
                                    <button
                                        onClick={() => mutation.mutate({ id: p.productId, fn: action.fn })}
                                        disabled={mutation.isPending}
                                        className={`px-4 py-2 text-white text-sm font-semibold rounded-lg transition shrink-0 ${action.color} disabled:bg-slate-300`}
                                    >
                                        {action.label}
                                    </button>
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>

            <div className="mt-8 p-4 bg-slate-50 rounded-xl text-xs text-slate-400 leading-relaxed">
                <p className="font-semibold text-slate-500 mb-1">상품 인가 흐름</p>
                설계완료 → <span className="text-blue-600 font-medium">인가 신청 (CT-04)</span> →
                인가완료 → <span className="text-indigo-600 font-medium">요율검증 요청 (CT-05)</span> →
                판매신청중 → <span className="text-green-600 font-medium">판매 확정 (CT-06)</span> → 판매중
            </div>
        </div>
    );
}
