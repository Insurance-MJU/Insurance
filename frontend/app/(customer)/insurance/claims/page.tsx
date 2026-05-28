'use client';
import { useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { reportAccident } from '@/queries/claims';
import { getSubscriptions } from '@/queries/subscriptions';
import type { Subscription } from '@/types';

export default function ClaimsPage() {
    const [contractId, setContractId] = useState('');
    const [reportedBy, setReportedBy] = useState('');
    const [phone, setPhone] = useState('');
    const [accidentDate, setAccidentDate] = useState('');
    const [accidentLocation, setAccidentLocation] = useState('');
    const [accidentDetail, setAccidentDetail] = useState('');
    const [submitted, setSubmitted] = useState(false);

    const { data: subscriptions } = useQuery<Subscription[]>({
        queryKey: ['subscriptions'],
        queryFn: getSubscriptions,
    });

    const approvedContracts = subscriptions?.filter(s => s.status === 'APPROVED' && s.contractId) ?? [];

    const mutation = useMutation({
        mutationFn: reportAccident,
        onSuccess: () => setSubmitted(true),
        onError: (e: any) => alert(`접수 실패: ${e.message}`),
    });

    if (submitted) {
        return (
            <div className="max-w-lg mx-auto px-4 py-20 text-center">
                <div className="text-5xl mb-4">✅</div>
                <h2 className="text-2xl font-extrabold text-slate-900 mb-2">사고 접수 완료</h2>
                <p className="text-slate-500">담당자가 확인 후 연락드릴 예정입니다.</p>
            </div>
        );
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        mutation.mutate({ contractId, reportedBy, phone, accidentDate: accidentDate.replace('T', ' '), accidentLocation, accidentDetail, documents: '' });
    };

    return (
        <div className="max-w-2xl mx-auto px-4 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">사고 접수</h1>
            <p className="text-slate-500 mb-10">사고 내용을 입력해 주세요</p>

            <form onSubmit={handleSubmit} className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 flex flex-col gap-5">
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">계약 선택</label>
                    {approvedContracts.length > 0 ? (
                        <select
                            required
                            value={contractId}
                            onChange={e => setContractId(e.target.value)}
                            className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">계약을 선택하세요</option>
                            {approvedContracts.map(s => (
                                <option key={s.contractId} value={s.contractId!}>
                                    {s.productName} ({s.contractId})
                                </option>
                            ))}
                        </select>
                    ) : (
                        <p className="text-sm text-red-500 bg-red-50 rounded-lg px-3 py-2.5">
                            승인된 계약이 없습니다. 보험 가입 후 이용해 주세요.
                        </p>
                    )}
                </div>
                <Field label="신고자 이름" value={reportedBy} onChange={setReportedBy} placeholder="홍길동" />
                <Field label="연락처" value={phone} onChange={setPhone} placeholder="010-0000-0000" />
                <Field label="사고 일시" value={accidentDate} onChange={setAccidentDate} type="datetime-local" />
                <Field label="사고 장소" value={accidentLocation} onChange={setAccidentLocation} placeholder="서울시 강남구 테헤란로 123" />
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">사고 내용</label>
                    <textarea
                        required
                        value={accidentDetail}
                        onChange={e => setAccidentDetail(e.target.value)}
                        rows={4}
                        className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                        placeholder="사고 상황을 자세히 설명해 주세요"
                    />
                </div>
                <button
                    type="submit"
                    disabled={mutation.isPending || approvedContracts.length === 0}
                    className="w-full py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-400 text-white font-bold rounded-xl transition mt-2"
                >
                    {mutation.isPending ? '접수 중...' : '사고 접수하기'}
                </button>
            </form>
        </div>
    );
}

function Field({ label, value, onChange, placeholder, type = 'text' }: {
    label: string; value: string; onChange: (v: string) => void; placeholder?: string; type?: string;
}) {
    return (
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <input
                type={type}
                required
                value={value}
                onChange={e => onChange(e.target.value)}
                placeholder={placeholder}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
        </div>
    );
}
