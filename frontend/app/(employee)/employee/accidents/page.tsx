'use client';
import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getAccidents, assignAccident, investigateDamage } from '@/queries/claims';
import type { Accident } from '@/types';

const STATUS_COLOR: Record<string, string> = {
    '접수':     'bg-blue-50 text-blue-700',
    '조사중':   'bg-yellow-50 text-yellow-700',
    '처리완료': 'bg-green-50 text-green-700',
};

export default function AccidentsPage() {
    const qc = useQueryClient();
    const [investigatingId, setInvestigatingId] = useState<string | null>(null);
    const [form, setForm] = useState({ opinion: '', damageCode: '', injuryGrade: 12, ourFault: 80, otherFault: 20, liability: '부책', finalOpinion: '' });

    const { data: accidents, isLoading } = useQuery<Accident[]>({
        queryKey: ['accidents'],
        queryFn: () => getAccidents(),
    });

    const assign = useMutation({
        mutationFn: ({ id, employeeId }: { id: string; employeeId: string }) => assignAccident(id, employeeId),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['accidents'] }),
    });
    const investigate = useMutation({
        mutationFn: ({ id, data }: { id: string; data: typeof form }) => investigateDamage(id, data),
        onSuccess: () => { qc.invalidateQueries({ queryKey: ['accidents'] }); setInvestigatingId(null); },
        onError: (e: any) => alert(e.message),
    });

    const handleAssign = (id: string) => {
        const employeeId = prompt('배정할 직원 ID를 입력하세요');
        if (employeeId) assign.mutate({ id, employeeId });
    };

    return (
        <div className="max-w-5xl mx-auto px-6 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">사고 접수 목록</h1>
            <p className="text-slate-500 mb-10">접수된 사고를 확인하고 손해 조사를 진행하세요</p>

            {isLoading && <p className="text-center py-20 text-slate-500">불러오는 중...</p>}
            {accidents?.length === 0 && <p className="text-center py-20 text-slate-400">접수된 사고가 없습니다.</p>}

            <div className="flex flex-col gap-4">
                {accidents?.map(a => (
                    <div key={a.accidentId} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex flex-col gap-4">
                        <div className="flex items-center justify-between gap-4">
                            <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                    <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${STATUS_COLOR[a.status] ?? 'bg-slate-50 text-slate-600'}`}>
                                        {a.status}
                                    </span>
                                    <span className="text-xs text-slate-400">{a.accidentId}</span>
                                </div>
                                <h3 className="font-bold text-slate-900">{a.accidentLocation}</h3>
                                <p className="text-sm text-slate-500">신고자: {a.reportedBy} · {a.phone} · {a.accidentDate}</p>
                            </div>
                            <div className="flex gap-2">
                                {a.status === '접수' && (
                                    <button onClick={() => handleAssign(a.accidentId)} disabled={assign.isPending}
                                        className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-lg transition">
                                        조사관 배정
                                    </button>
                                )}
                                <button
                                    onClick={() => setInvestigatingId(investigatingId === a.accidentId ? null : a.accidentId)}
                                    className="px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white text-sm font-semibold rounded-lg transition">
                                    손해 조사 (CL-03)
                                </button>
                            </div>
                        </div>

                        {/* CL-03 손해 조사 폼 */}
                        {investigatingId === a.accidentId && (
                            <div className="bg-slate-50 rounded-xl p-5 border border-slate-200 flex flex-col gap-4">
                                <p className="font-semibold text-slate-700">손해 조사 입력 (CL-03)</p>
                                <div className="grid grid-cols-2 gap-3">
                                    <Field label="현장 조사 소견" value={form.opinion} onChange={v => setForm(f => ({...f, opinion: v}))} placeholder="블랙박스 분석 결과..." />
                                    <Field label="파손 부위 코드" value={form.damageCode} onChange={v => setForm(f => ({...f, damageCode: v}))} placeholder="CAR-D-03" />
                                    <div>
                                        <label className="block text-xs font-medium text-gray-700 mb-1">부상 급수 (1~14급)</label>
                                        <input type="number" min={1} max={14} value={form.injuryGrade}
                                            onChange={e => setForm(f => ({...f, injuryGrade: Number(e.target.value)}))}
                                            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500" />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-medium text-gray-700 mb-1">면/부책 여부</label>
                                        <select value={form.liability} onChange={e => setForm(f => ({...f, liability: e.target.value}))}
                                            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500">
                                            <option value="부책">부책</option>
                                            <option value="면책">면책</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label className="block text-xs font-medium text-gray-700 mb-1">당사 과실 (%)</label>
                                        <input type="number" min={0} max={100} value={form.ourFault}
                                            onChange={e => setForm(f => ({...f, ourFault: Number(e.target.value), otherFault: 100 - Number(e.target.value)}))}
                                            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500" />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-medium text-gray-700 mb-1">타사 과실 (%)</label>
                                        <input type="number" readOnly value={form.otherFault}
                                            className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm bg-gray-50 text-gray-500" />
                                    </div>
                                </div>
                                <Field label="최종 조사 의견" value={form.finalOpinion} onChange={v => setForm(f => ({...f, finalOpinion: v}))} placeholder="합의금 산출 진행 요망" />
                                <div className="flex gap-2">
                                    <button
                                        onClick={() => investigate.mutate({ id: a.accidentId, data: form })}
                                        disabled={investigate.isPending || !form.damageCode || !form.opinion}
                                        className="px-5 py-2 bg-purple-600 hover:bg-purple-700 disabled:bg-slate-300 text-white text-sm font-bold rounded-lg transition">
                                        {investigate.isPending ? '저장 중...' : '조사 완료 및 저장'}
                                    </button>
                                    <button onClick={() => setInvestigatingId(null)} className="px-5 py-2 text-sm text-slate-400 hover:text-slate-600">취소</button>
                                </div>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

function Field({ label, value, onChange, placeholder }: { label: string; value: string; onChange: (v: string) => void; placeholder?: string }) {
    return (
        <div>
            <label className="block text-xs font-medium text-gray-700 mb-1">{label}</label>
            <input type="text" value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder}
                className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500" />
        </div>
    );
}
