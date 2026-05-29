'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { fetchApi } from '@/queries/api';

const STATUS_META: Record<string, { label: string; color: string }> = {
    '접수 완료':     { label: '접수 완료',     color: 'bg-gray-100 text-gray-600' },
    '현장 조사 중':  { label: '현장 조사 중',  color: 'bg-yellow-100 text-yellow-700' },
    '손해 산정 완료': { label: '손해 산정 완료', color: 'bg-blue-100 text-blue-700' },
    '지급 완료':     { label: '지급 완료',     color: 'bg-green-100 text-green-700' },
    // 하위 호환
    '미처리':        { label: '접수 완료',     color: 'bg-gray-100 text-gray-600' },
    '보상팀 이관':   { label: '현장 조사 중',  color: 'bg-yellow-100 text-yellow-700' },
    '처리중':        { label: '처리중',        color: 'bg-indigo-100 text-indigo-700' },
    '지급 종결':     { label: '지급 완료',     color: 'bg-green-100 text-green-700' },
};

export default function AccidentsPage() {
    const [accidents, setAccidents] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let mounted = true;
        (async () => {
            setLoading(true);
            try {
                const res = await fetchApi('/accidents');
                const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
                if (mounted) setAccidents(list);
            } catch {
                if (mounted) setAccidents([]);
            } finally {
                if (mounted) setLoading(false);
            }
        })();
        return () => { mounted = false; };
    }, []);

    return (
        <main className="max-w-2xl mx-auto p-4">
            <div className="flex items-center justify-between mb-6">
                <h1 className="text-2xl font-bold">사고 접수 내역</h1>
                <Link href="/insurance/contracts" className="text-sm text-gray-500 hover:underline">
                    ← 계약 현황
                </Link>
            </div>

            {loading ? (
                <div className="border rounded-lg p-6 text-center text-gray-400 text-sm">로딩 중...</div>
            ) : accidents.length === 0 ? (
                <div className="border rounded-lg p-8 text-center text-gray-400">
                    <p className="text-lg mb-2">접수된 사고가 없습니다.</p>
                    <Link href="/insurance/contracts" className="text-sm text-blue-500 hover:underline">
                        계약 현황에서 사고 접수하기
                    </Link>
                </div>
            ) : (
                <div className="flex flex-col gap-3">
                    {accidents.map((a) => {
                        const badge = STATUS_META[a.status] ?? { label: a.status, color: 'bg-gray-100 text-gray-600' };
                        return (
                            <div key={a.accidentId} className="border rounded-lg p-5">
                                <div className="flex justify-between items-start mb-2">
                                    <div>
                                        <p className="font-semibold text-sm">{a.accidentId}</p>
                                        <p className="text-xs text-gray-400 mt-0.5">{a.accidentDate}</p>
                                    </div>
                                    <span className={`px-2 py-1 text-xs rounded font-bold ${badge.color}`}>
                                        {badge.label}
                                    </span>
                                </div>
                                <p className="text-sm text-gray-600 mb-1">
                                    장소: {a.accidentLocation || '—'}
                                </p>
                                <p className="text-xs text-gray-400">
                                    계약번호: {a.contractId || '—'}
                                </p>
                            </div>
                        );
                    })}
                </div>
            )}
        </main>
    );
}
