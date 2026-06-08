'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { fetchApi } from '@/queries/api';

const STEPS = ['접수 완료', '현장 조사 중', '손해 산정 완료', '지급 완료'];

const STEP_ICONS = ['📋', '🔎', '📊', '✅'];

const STATUS_TO_STEP: Record<string, number> = {
    '접수 완료':      0,
    '미처리':         0,
    '현장 조사 중':   1,
    '보상팀 이관':    1,
    '처리중':         2,
    '손해 산정 완료': 2,
    '지급 완료':      3,
    '지급 종결':      3,
};

export default function AccidentDetailPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const [accident, setAccident] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let mounted = true;
        (async () => {
            try {
                const res = await fetchApi(`/accidents/${id}`);
                if (mounted) setAccident(res.data ?? res);
            } catch {
                if (mounted) setAccident(null);
            } finally {
                if (mounted) setLoading(false);
            }
        })();
        return () => { mounted = false; };
    }, [id]);

    if (loading) {
        return <main className="max-w-xl mx-auto p-4 mt-8 text-center text-gray-400 text-sm">로딩 중...</main>;
    }
    if (!accident) {
        return (
            <main className="max-w-xl mx-auto p-4 mt-8 text-center">
                <p className="text-gray-500 mb-4">접수 내역을 찾을 수 없습니다.</p>
                <button onClick={() => router.back()} className="text-sm text-blue-500 hover:underline">← 돌아가기</button>
            </main>
        );
    }

    const currentStep = STATUS_TO_STEP[accident.status] ?? 0;

    return (
        <main className="max-w-xl mx-auto p-4">
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-xl font-bold">청구 상세 현황</h1>
                <button onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
            </div>

            {/* 기본 정보 */}
            <div className="border rounded-xl p-5 bg-white mb-6 space-y-2">
                <div className="flex justify-between items-start">
                    <p className="font-bold text-base">{accident.accidentId}</p>
                    <span className="text-xs text-gray-400">{accident.accidentDate}</span>
                </div>
                <p className="text-sm text-gray-600">장소: {accident.accidentLocation || '—'}</p>
                <p className="text-sm text-gray-600">계약번호: {accident.contractId || '—'}</p>
                {accident.reportedBy && (
                    <p className="text-sm text-gray-600">접수자: {accident.reportedBy}</p>
                )}
            </div>

            {/* 진행 단계 */}
            <div className="border rounded-xl p-6 bg-white">
                <p className="text-sm font-semibold text-gray-700 mb-6">처리 진행 단계</p>

                <div className="relative flex items-start justify-between">
                    {/* 연결선 */}
                    <div className="absolute top-5 left-0 right-0 h-0.5 bg-gray-200 z-0" style={{ margin: '0 2rem' }} />
                    <div
                        className="absolute top-5 left-0 h-0.5 bg-blue-500 z-0 transition-all duration-500"
                        style={{
                            margin: '0 2rem',
                            width: currentStep === 0 ? '0%'
                                : currentStep === 1 ? '33.3%'
                                : currentStep === 2 ? '66.6%'
                                : '100%',
                        }}
                    />

                    {STEPS.map((step, i) => {
                        const done    = i < currentStep;
                        const current = i === currentStep;
                        return (
                            <div key={step} className="relative z-10 flex flex-col items-center gap-2 flex-1">
                                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-lg border-2 transition-all
                                    ${done    ? 'bg-blue-500 border-blue-500 text-white'
                                    : current ? 'bg-white border-blue-500 shadow-md shadow-blue-200'
                                    : 'bg-white border-gray-200 text-gray-300'}`}>
                                    {done ? '✓' : STEP_ICONS[i]}
                                </div>
                                <p className={`text-xs text-center leading-tight font-medium
                                    ${done ? 'text-blue-500' : current ? 'text-blue-700 font-bold' : 'text-gray-300'}`}>
                                    {step}
                                </p>
                                {current && (
                                    <span className="text-xs bg-blue-100 text-blue-600 px-2 py-0.5 rounded-full font-semibold">진행 중</span>
                                )}
                            </div>
                        );
                    })}
                </div>

                <p className="mt-8 text-center text-sm text-gray-500">
                    현재 단계: <span className="font-bold text-blue-600">{STEPS[currentStep]}</span>
                </p>
            </div>
        </main>
    );
}
