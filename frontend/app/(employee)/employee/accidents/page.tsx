'use client';
import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getAccidents, getInvestigators, assignAccident, investigateDamage } from '@/queries/claims';
import type { Accident, Investigator } from '@/types';

const STATUS_COLOR: Record<string, string> = {
    '접수':     'bg-blue-50 text-blue-700',
    '조사중':   'bg-yellow-50 text-yellow-700',
    '처리완료': 'bg-green-50 text-green-700',
};

export default function AccidentsPage() {
    const qc = useQueryClient();

    // 조사관 배정 패널 상태
    const [assigningId, setAssigningId] = useState<string | null>(null);
    const [specialty, setSpecialty] = useState('');
    const [searchedSpecialty, setSearchedSpecialty] = useState<string | undefined>(undefined);

    // 손해 조사 패널 상태
    const [investigatingId, setInvestigatingId] = useState<string | null>(null);
    const [form, setForm] = useState({ opinion: '', damageCode: '', injuryGrade: 12, ourFault: 80, otherFault: 20, liability: '부책', finalOpinion: '' });

    const { data: accidents, isLoading } = useQuery<Accident[]>({
        queryKey: ['accidents'],
        queryFn: () => getAccidents(),
    });

    const { data: investigators, isFetching: investigatorsFetching } = useQuery<Investigator[]>({
        queryKey: ['investigators', searchedSpecialty],
        queryFn: () => getInvestigators(searchedSpecialty),
        enabled: assigningId !== null,
    });

    const assign = useMutation({
        mutationFn: ({ id, employeeId }: { id: string; employeeId: string }) => assignAccident(id, employeeId),
        onSuccess: () => {
            qc.invalidateQueries({ queryKey: ['accidents'] });
            setAssigningId(null);
            setSpecialty('');
            setSearchedSpecialty(undefined);
        },
    });

    const investigate = useMutation({
        mutationFn: ({ id, data }: { id: string; data: typeof form }) => investigateDamage(id, data),
        onSuccess: () => { qc.invalidateQueries({ queryKey: ['accidents'] }); setInvestigatingId(null); },
        onError: (e: any) => alert(e.message),
    });

    const openAssignPanel = (id: string) => {
        setAssigningId(id);
        setSpecialty('');
        setSearchedSpecialty(undefined);
        setInvestigatingId(null);
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
                                    <button
                                        onClick={() => assigningId === a.accidentId ? setAssigningId(null) : openAssignPanel(a.accidentId)}
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

                        {/* CL-01 조사관 배정 패널 */}
                        {assigningId === a.accidentId && (
                            <div className="bg-blue-50 rounded-xl p-5 border border-blue-200 flex flex-col gap-4">
                                <p className="font-semibold text-blue-800">배당 담당자 검색 (CL-01)</p>

                                <div className="flex gap-2">
                                    <input
                                        type="text"
                                        value={specialty}
                                        onChange={e => setSpecialty(e.target.value)}
                                        placeholder="전문 분야 (예: 자동차 대물)"
                                        className="flex-1 border border-blue-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    />
                                    <button
                                        onClick={() => setSearchedSpecialty(specialty || undefined)}
                                        className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-lg transition">
                                        검색
                                    </button>
                                </div>

                                {investigatorsFetching && (
                                    <p className="text-sm text-blue-500">검색 중...</p>
                                )}

                                {!investigatorsFetching && investigators && investigators.length === 0 && (
                                    <p className="text-sm text-slate-400">해당 조건의 현장조사역이 없습니다.</p>
                                )}

                                {!investigatorsFetching && investigators && investigators.length > 0 && (
                                    <div className="flex flex-col gap-2">
                                        <p className="text-xs font-medium text-slate-600">현장조사역 후보 목록 (출동 가능 직원명 / 미결 건수)</p>
                                        {investigators.map(inv => (
                                            <div key={inv.employeeId} className="flex items-center justify-between bg-white rounded-lg px-4 py-3 border border-blue-100">
                                                <div>
                                                    <span className="font-semibold text-slate-800">{inv.name}</span>
                                                    <span className="ml-2 text-xs text-slate-500">{inv.specialty}</span>
                                                </div>
                                                <div className="flex items-center gap-3">
                                                    <span className="text-sm text-slate-500">미결 <strong>{inv.openCaseCount}</strong>건</span>
                                                    <button
                                                        onClick={() => assign.mutate({ id: a.accidentId, employeeId: inv.employeeId })}
                                                        disabled={assign.isPending}
                                                        className="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white text-xs font-bold rounded-lg transition">
                                                        배정
                                                    </button>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}

                                <button onClick={() => setAssigningId(null)} className="text-sm text-slate-400 hover:text-slate-600 self-start">취소</button>
                            </div>
                        )}

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
