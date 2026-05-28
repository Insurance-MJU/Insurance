'use client';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getClaims, assessClaim, payClaim, getInvestigation } from '@/queries/claims';
import type { Claim } from '@/types';

function ClaimRow({ c, onAssess, onPay, assessPending, payPending }: {
    c: Claim;
    onAssess: (id: string) => void;
    onPay: (id: string) => void;
    assessPending: boolean;
    payPending: boolean;
}) {
    // CL-02는 CL-03(손해 조사) 완료 후에만 가능 — accidentId로 조사 여부 확인
    const { data: inv } = useQuery({
        queryKey: ['investigation', c.accidentId],
        queryFn: () => getInvestigation(c.accidentId),
        enabled: c.status === 'INVESTIGATING' && !!c.accidentId,
    });
    const investigationDone = inv?.exists === true;

    return (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 flex items-center justify-between gap-4">
            <div className="flex-1">
                <p className="text-xs text-slate-400 mb-1">{c.claimId} · {c.claimDate}</p>
                <h3 className="font-bold text-slate-900">{c.description}</h3>
                <p className="text-sm text-slate-500">청구자: {c.claimantName} · 계약: {c.contractId}</p>
                {c.compensationAmount != null && (
                    <p className="text-sm text-blue-600 font-semibold mt-1">
                        결정액: {c.compensationAmount.toLocaleString()}원
                        {c.deductibleAmount ? ` (공제 ${c.deductibleAmount.toLocaleString()}원)` : ''}
                    </p>
                )}
            </div>
            <div className="flex flex-col items-end gap-1.5">
                {c.status === 'INVESTIGATING' && (
                    <>
                        {!investigationDone && (
                            <p className="text-xs text-amber-600">⚠ 손해 조사(CL-03) 먼저 완료하세요</p>
                        )}
                        <button
                            onClick={() => onAssess(c.claimId)}
                            disabled={assessPending || !investigationDone}
                            className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white text-sm font-semibold rounded-lg transition"
                        >
                            손해사정 (CL-02)
                        </button>
                    </>
                )}
                {c.status === 'AWAITING_PAYMENT' && (
                    <button
                        onClick={() => onPay(c.claimId)}
                        disabled={payPending}
                        className="px-4 py-2 bg-green-600 hover:bg-green-700 disabled:bg-slate-300 text-white text-sm font-semibold rounded-lg transition"
                    >
                        지급 처리 (CL-04)
                    </button>
                )}
                {c.status === 'PAID' && (
                    <span className="px-4 py-2 bg-slate-100 text-slate-500 text-sm font-semibold rounded-lg">지급 완료</span>
                )}
            </div>
        </div>
    );
}

export default function ClaimsPage() {
    const qc = useQueryClient();
    const { data: claims, isLoading } = useQuery<Claim[]>({
        queryKey: ['claims'],
        queryFn: getClaims,
    });

    const assess = useMutation({
        mutationFn: ({ id, settlement, deductible }: { id: string; settlement: number; deductible: number }) =>
            assessClaim(id, { settlement, deductible }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['claims'] }),
    });
    const pay = useMutation({
        mutationFn: ({ id, bank, accountNo }: { id: string; bank: string; accountNo: string }) =>
            payClaim(id, { bank, accountNo }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ['claims'] }),
    });

    const handleAssess = (id: string) => {
        const settlement = Number(prompt('지급 결정액 (원)'));
        const deductible = Number(prompt('공제액 (원)', '0'));
        if (!isNaN(settlement)) assess.mutate({ id, settlement, deductible });
    };

    const handlePay = (id: string) => {
        const bank = prompt('은행명 (예: 국민은행)');
        const accountNo = prompt('계좌번호');
        if (bank && accountNo) pay.mutate({ id, bank, accountNo });
    };

    return (
        <div className="max-w-5xl mx-auto px-6 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">보험금 처리</h1>
            <p className="text-slate-500 mb-2">손해사정 및 보험금 지급을 처리하세요</p>
            <p className="text-xs text-slate-400 mb-10">순서: 손해 조사(CL-03) → 손해사정(CL-02) → 보험금 지급(CL-04)</p>

            {isLoading && <p className="text-center py-20 text-slate-500">불러오는 중...</p>}
            {claims?.length === 0 && <p className="text-center py-20 text-slate-400">처리할 보험금 청구가 없습니다.</p>}

            <div className="flex flex-col gap-4">
                {claims?.map(c => (
                    <ClaimRow key={c.claimId} c={c}
                        onAssess={handleAssess} onPay={handlePay}
                        assessPending={assess.isPending} payPending={pay.isPending} />
                ))}
            </div>
        </div>
    );
}
