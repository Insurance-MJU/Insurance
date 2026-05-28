'use client';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getSubscriptions } from '@/queries/subscriptions';
import { getAccidents } from '@/queries/claims';
import type { Subscription, Accident } from '@/types';

const STATUS_LABEL: Record<string, { label: string; color: string }> = {
    PENDING_REVIEW: { label: '심사 중',    color: 'bg-yellow-50 text-yellow-700' },
    APPROVED:       { label: '승인',       color: 'bg-green-50 text-green-700' },
    REJECTED:       { label: '거절',       color: 'bg-red-50 text-red-700' },
    SUPPLEMENT:     { label: '보완 요청',  color: 'bg-orange-50 text-orange-700' },
};

export default function ContractsPage() {
    const [expandedNo, setExpandedNo] = useState<string | null>(null);

    const { data: subscriptions, isLoading } = useQuery<Subscription[]>({
        queryKey: ['subscriptions'],
        queryFn: getSubscriptions,
    });
    const { data: accidents } = useQuery<Accident[]>({
        queryKey: ['accidents'],
        queryFn: () => getAccidents(),
    });

    return (
        <div className="max-w-4xl mx-auto px-4 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">내 계약 (CS-05)</h1>
            <p className="text-slate-500 mb-10">계약 · 보상 이력을 통합 조회하세요</p>

            {isLoading && <p className="text-center py-20 text-slate-500">불러오는 중...</p>}
            {subscriptions?.length === 0 && <p className="text-center py-20 text-slate-400">가입 내역이 없습니다.</p>}

            <div className="flex flex-col gap-4">
                {subscriptions?.map(s => {
                    const relatedAccidents = accidents?.filter(a => a.contractId === s.contractId) ?? [];
                    const isExpanded = expandedNo === s.subscriptionNo;
                    const statusInfo = STATUS_LABEL[s.status] ?? { label: s.status, color: 'bg-slate-50 text-slate-600' };

                    return (
                        <div key={s.subscriptionNo} className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
                            {/* 계약 기본 정보 */}
                            <div className="p-6 flex items-center justify-between gap-4">
                                <div className="flex-1">
                                    <div className="flex items-center gap-2 mb-1">
                                        <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${statusInfo.color}`}>{statusInfo.label}</span>
                                        <span className="text-xs text-slate-400">{s.subscriptionNo}</span>
                                    </div>
                                    <h3 className="font-bold text-slate-900">{s.productName}</h3>
                                    <p className="text-sm text-slate-500 mt-0.5">신청자: {s.applicantName} · {s.subscriptionDate}</p>
                                    {s.contractId && (
                                        <p className="text-xs text-blue-600 font-semibold mt-1">계약번호: {s.contractId}</p>
                                    )}
                                </div>
                                <div className="text-right">
                                    <p className="text-lg font-extrabold text-slate-900">{s.premium.toLocaleString()}원</p>
                                    <p className="text-xs text-slate-400 mb-2">월 납입</p>
                                    {s.contractId && (
                                        <button
                                            onClick={() => setExpandedNo(isExpanded ? null : s.subscriptionNo)}
                                            className="text-xs text-blue-600 hover:text-blue-800 font-semibold"
                                        >
                                            {isExpanded ? '접기 ▲' : '보상 이력 보기 ▼'}
                                        </button>
                                    )}
                                </div>
                            </div>

                            {/* 보상 이력 (통합 조회) */}
                            {isExpanded && (
                                <div className="border-t border-gray-100 bg-slate-50 px-6 py-4">
                                    <p className="text-sm font-semibold text-slate-700 mb-3">보상 이력</p>
                                    {relatedAccidents.length === 0 ? (
                                        <p className="text-sm text-slate-400">접수된 사고가 없습니다.</p>
                                    ) : (
                                        <div className="flex flex-col gap-2">
                                            {relatedAccidents.map(a => (
                                                <div key={a.accidentId} className="bg-white rounded-xl border border-gray-100 p-4 text-sm">
                                                    <div className="flex items-center justify-between mb-1">
                                                        <span className="font-semibold text-slate-900">{a.accidentLocation}</span>
                                                        <span className="text-xs text-slate-400">{a.accidentId}</span>
                                                    </div>
                                                    <p className="text-slate-500">사고일시: {a.accidentDate}</p>
                                                    <p className="text-slate-500">신고자: {a.reportedBy} · {a.phone}</p>
                                                    <span className={`mt-1 inline-block text-xs font-semibold px-2 py-0.5 rounded-full
                                                        ${a.status === '처리완료' ? 'bg-green-50 text-green-700' :
                                                          a.status === '조사중' ? 'bg-yellow-50 text-yellow-700' :
                                                          'bg-blue-50 text-blue-700'}`}>
                                                        {a.status}
                                                    </span>
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
