'use client';
import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getPendingSubscriptions, approveSubscription, rejectSubscription, supplementSubscription, analyzeRisk } from '@/queries/subscriptions';
import type { Subscription } from '@/types';

export default function SubscriptionsPage() {
    const qc = useQueryClient();
    const [riskResult, setRiskResult] = useState<Record<string, any> | null>(null);
    const [selectedNo, setSelectedNo] = useState('');

    const { data: list, isLoading } = useQuery<Subscription[]>({
        queryKey: ['subscriptions', 'pending'],
        queryFn: getPendingSubscriptions,
    });

    const approve = useMutation({
        mutationFn: (no: string) => approveSubscription(no),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['subscriptions'] }),
    });
    const reject = useMutation({
        mutationFn: ({ no, reason }: { no: string; reason: string }) => rejectSubscription(no, reason),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['subscriptions'] }),
    });
    const supplement = useMutation({
        mutationFn: ({ no, reason }: { no: string; reason: string }) => supplementSubscription(no, reason),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['subscriptions'] }),
    });
    const riskAnalysis = useMutation({
        mutationFn: (no: string) => analyzeRisk(no),
        onSuccess: (data, no) => { setRiskResult(data); setSelectedNo(no); },
        onError: (e: any) => alert(e.message),
    });

    const handleReject = (no: string) => {
        const reason = prompt('반려 사유를 입력하세요');
        if (reason) reject.mutate({ no, reason });
    };
    const handleSupplement = (no: string) => {
        const reason = prompt('보완 요청 사유를 입력하세요');
        if (reason) supplement.mutate({ no, reason });
    };

    return (
        <div className="max-w-5xl mx-auto px-6 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">청약 심사</h1>
            <p className="text-slate-500 mb-10">심사 대기 중인 가입 신청을 처리하세요</p>

            {isLoading && <p className="text-center py-20 text-slate-500">불러오는 중...</p>}
            {list?.length === 0 && <p className="text-center py-20 text-slate-400">심사 대기 중인 청약이 없습니다.</p>}

            <div className="flex flex-col gap-4">
                {list?.map(s => (
                    <div key={s.subscriptionNo} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex flex-col gap-4">
                        <div className="flex items-center justify-between gap-4">
                            <div className="flex-1">
                                <p className="text-xs text-slate-400 mb-1">{s.subscriptionNo} · {s.subscriptionDate}</p>
                                <h3 className="font-bold text-slate-900">{s.productName}</h3>
                                <p className="text-sm text-slate-500">신청자: {s.applicantName} · 보험료: {s.premium.toLocaleString()}원</p>
                            </div>
                            <div className="flex gap-2 flex-wrap justify-end">
                                <button
                                    onClick={() => riskAnalysis.mutate(s.subscriptionNo)}
                                    disabled={riskAnalysis.isPending}
                                    className="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white text-sm font-semibold rounded-lg transition"
                                >
                                    위험성 분석 (UW-02)
                                </button>
                                <button
                                    onClick={() => approve.mutate(s.subscriptionNo)}
                                    disabled={approve.isPending}
                                    className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-lg transition"
                                >
                                    승인
                                </button>
                                <button
                                    onClick={() => handleSupplement(s.subscriptionNo)}
                                    disabled={supplement.isPending}
                                    className="px-4 py-2 bg-white hover:bg-yellow-50 text-yellow-600 border border-yellow-200 text-sm font-semibold rounded-lg transition"
                                >
                                    보완
                                </button>
                                <button
                                    onClick={() => handleReject(s.subscriptionNo)}
                                    disabled={reject.isPending}
                                    className="px-4 py-2 bg-white hover:bg-red-50 text-red-600 border border-red-200 text-sm font-semibold rounded-lg transition"
                                >
                                    반려
                                </button>
                            </div>
                        </div>

                        {/* UW-02 위험 분석 결과 */}
                        {riskResult && selectedNo === s.subscriptionNo && (() => {
                            const surchargeAmt = Math.round(s.premium * riskResult.surchargeRate);
                            const finalPremium = s.premium + surchargeAmt;
                            return (
                                <div className="bg-slate-50 rounded-xl p-4 text-sm border border-slate-200">
                                    <p className="font-semibold text-slate-700 mb-3">위험성 분석 결과 (UW-02)</p>
                                    <div className="grid grid-cols-3 gap-2 mb-3">
                                        <div className="bg-white rounded-lg p-3 text-center border border-slate-100">
                                            <p className="text-xs text-slate-400 mb-1">위험 등급</p>
                                            <p className="text-xl font-extrabold text-slate-900">{riskResult.riskGradeLabel}</p>
                                        </div>
                                        <div className="bg-white rounded-lg p-3 text-center border border-slate-100">
                                            <p className="text-xs text-slate-400 mb-1">할증율</p>
                                            <p className="text-xl font-extrabold text-orange-500">+{(riskResult.surchargeRate * 100).toFixed(0)}%</p>
                                        </div>
                                        <div className="bg-white rounded-lg p-3 text-center border border-slate-100">
                                            <p className="text-xs text-slate-400 mb-1">위험 점수</p>
                                            <p className="text-xl font-extrabold text-slate-900">{riskResult.riskScore?.toFixed(1)}점</p>
                                        </div>
                                    </div>
                                    {/* 할증 보험료 계산 */}
                                    <div className="bg-white rounded-xl border border-slate-100 p-3 mb-3 text-xs space-y-1.5">
                                        <div className="flex justify-between text-slate-500">
                                            <span>기본 보험료</span>
                                            <span>{s.premium.toLocaleString()}원</span>
                                        </div>
                                        <div className="flex justify-between text-orange-500">
                                            <span>위험 할증 (+{(riskResult.surchargeRate * 100).toFixed(0)}%)</span>
                                            <span>+{surchargeAmt.toLocaleString()}원</span>
                                        </div>
                                        <div className="flex justify-between font-extrabold text-slate-900 border-t border-slate-100 pt-1.5">
                                            <span>최종 보험료</span>
                                            <span>{finalPremium.toLocaleString()}원</span>
                                        </div>
                                    </div>
                                    {riskResult.creditInfo?.newApplicant ? (
                                        <p className="text-slate-500 text-xs">신규 가입자 — 기본 위험등급(3등급) 적용</p>
                                    ) : (
                                        <div className="text-xs text-slate-500 space-y-1">
                                            <p>사고이력: {riskResult.creditInfo?.accidentCount}건 · 운전경력: {riskResult.creditInfo?.drivingExperienceYears}년</p>
                                            <p>신용등급: {riskResult.creditInfo?.creditGrade} · 보험사기: {riskResult.creditInfo?.fraudHistory}</p>
                                        </div>
                                    )}
                                    <button onClick={() => setRiskResult(null)} className="mt-3 text-xs text-slate-400 hover:text-slate-600">닫기</button>
                                </div>
                            );
                        })()}
                    </div>
                ))}
            </div>
        </div>
    );
}
