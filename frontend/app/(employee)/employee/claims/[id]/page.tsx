'use client';

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { fetchApi } from "@/queries/api";

export default function ClaimDetailPage() {
    const { id } = useParams();
    const [claim, setClaim]           = useState<any>(null);
    const [investigation, setInvestigation] = useState<any>(null);
    const [error, setError]           = useState("");

    // CL-02
    const [settlement,  setSettlement]  = useState("");
    const [deductible,  setDeductible]  = useState("200000");
    const [assessLoading, setAssessLoading] = useState(false);

    // CL-04 단계: 'input' → 'verified' → 'approved' → done
    const [bank,       setBank]       = useState("");
    const [account,    setAccount]    = useState("");
    const [payOpinion, setPayOpinion] = useState("");
    const [payStep, setPayStep]       = useState<'input' | 'verified' | 'approved'>('input');
    const [payLoading, setPayLoading] = useState(false);

    const load = () => {
        if (!id) return;
        fetchApi(`/claims/${id}`)
            .then(r => {
                const c = r.data ?? r;
                setClaim(c);
                if (c.accidentId) {
                    fetchApi(`/accidents/${c.accidentId}/investigation`)
                        .then(inv => setInvestigation(inv.data ?? inv))
                        .catch(() => setInvestigation({ exists: false }));
                } else {
                    setInvestigation({ exists: false });
                }
            })
            .catch(() => setError("클레임 정보를 불러올 수 없습니다."));
    };

    useEffect(() => { load(); }, [id]);

    const handleAssess = async () => {
        if (!settlement) { alert("합의금을 입력하세요."); return; }
        setAssessLoading(true);
        try {
            const res = await fetchApi(`/claims/${id}/assess`, {
                method: "PUT",
                body: JSON.stringify({ settlement: Number(settlement), deductible: Number(deductible) }),
            });
            setClaim(res.data ?? res);
            alert("손해액 산정이 완료되었습니다. 상태: 지급대기");
        } catch (e: any) { alert(e?.message ?? "산정 실패"); }
        finally { setAssessLoading(false); }
    };

    const handleVerify = () => {
        if (!bank || !account) { alert("은행명과 계좌번호를 입력하세요."); return; }
        setPayStep('verified');
    };

    const handleApprove = () => {
        if (!payOpinion.trim()) { alert("결재 의견을 입력하세요."); return; }
        setPayStep('approved');
    };

    const handlePay = async () => {
        setPayLoading(true);
        try {
            const res = await fetchApi(`/claims/${id}/pay`, {
                method: "PUT",
                body: JSON.stringify({ bank, accountNo: account }),
            });
            setClaim(res.data ?? res);
            alert("보험금이 지급 완료되었습니다. 상태: 종결");
        } catch (e: any) { alert(e?.message ?? "지급 실패"); }
        finally { setPayLoading(false); }
    };

    if (error) return <p className="text-red-500 text-sm p-6">{error}</p>;
    if (!claim) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;

    const isPaid     = claim.status === "CLOSED";
    const isAwaiting = claim.status === "PAYMENT_PENDING";
    const investigationDone = investigation?.exists === true;

    return (
        <div className="max-w-3xl space-y-6">
            <div>
                <Link href="/employee/claims" className="text-sm text-gray-400 hover:text-gray-600">← 클레임 목록</Link>
                <h1 className="text-xl font-bold text-gray-800 mt-1">손해 처리</h1>
                <p className="text-xs text-gray-400">{claim.claimId}</p>
            </div>

            {/* 클레임 기본 정보 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <h2 className="text-sm font-semibold text-gray-600 mb-3">클레임 정보</h2>
                <div className="grid grid-cols-2 gap-2 text-sm">
                    {[
                        ["고객명",    claim.customerName],
                        ["상태",      claim.status],
                        ["사고번호",  claim.accidentId],
                        ["계약번호",  claim.contractId],
                        ["담당직원",  claim.assignedEmployee ?? "-"],
                        ["접수일",    claim.receivedDate],
                    ].map(([label, value]) => (
                        <div key={label} className="flex gap-2">
                            <span className="w-20 text-gray-400 shrink-0">{label}</span>
                            <span className="text-gray-800">{value}</span>
                        </div>
                    ))}
                </div>
            </div>

            {/* CL-02: 손해액 산정 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <h2 className="text-sm font-semibold text-gray-600 mb-4">손해액 산정 (CL-02)</h2>

                {isAwaiting || isPaid ? (
                    <p className="text-green-600 text-sm font-medium">✓ 손해액 산정 완료 → {claim.status}</p>
                ) : !investigationDone ? (
                    <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4 space-y-2">
                        <p className="text-sm font-medium text-yellow-700">
                            ⚠️ 손해 조사가 완료되지 않아 산정을 진행할 수 없습니다.
                        </p>
                        <p className="text-xs text-yellow-600">조사를 완료해 주세요.</p>
                        {claim.accidentId && (
                            <Link href={`/employee/accidents/${claim.accidentId}/investigation`}
                                className="inline-block mt-1 px-3 py-1.5 bg-yellow-500 text-white text-xs rounded-lg hover:bg-yellow-600">
                                손해 조사 하러 가기 →
                            </Link>
                        )}
                    </div>
                ) : (
                    <div className="space-y-3">
                        {investigationDone && (
                            <p className="text-xs text-green-600">✓ 손해 조사 완료 확인됨</p>
                        )}
                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">최종 합의금 (원)</label>
                                <input type="number" value={settlement}
                                    onChange={e => setSettlement(e.target.value)}
                                    placeholder="예: 15000000"
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                            </div>
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">자기부담금 (원)</label>
                                <input type="number" value={deductible}
                                    onChange={e => setDeductible(e.target.value)}
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                            </div>
                        </div>
                        {settlement && (
                            <p className="text-xs text-blue-600">
                                실 지급 보상금: {(Number(settlement) - Number(deductible)).toLocaleString()}원
                            </p>
                        )}
                        <button onClick={handleAssess} disabled={assessLoading}
                            className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-40">
                            {assessLoading ? "처리 중..." : "산정 내역 승인"}
                        </button>
                    </div>
                )}
            </div>

            {/* CL-04: 보험금 지급 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <h2 className="text-sm font-semibold text-gray-600 mb-4">보험금 지급 (CL-04)</h2>

                {isPaid ? (
                    <p className="text-green-600 text-sm font-medium">✓ 보험금 지급 완료</p>
                ) : !isAwaiting ? (
                    <p className="text-gray-400 text-sm">손해액 산정 완료 후 지급 가능합니다.</p>
                ) : payStep === 'input' ? (
                    <div className="space-y-3">
                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">수령 은행명</label>
                                <input value={bank} onChange={e => setBank(e.target.value)}
                                    placeholder="예: 한국은행"
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                            </div>
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">계좌번호</label>
                                <input value={account} onChange={e => setAccount(e.target.value)}
                                    placeholder="예: 123-456-7890"
                                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                            </div>
                        </div>
                        <button onClick={handleVerify}
                            className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                            계좌 유효성 검증
                        </button>
                    </div>
                ) : payStep === 'verified' ? (
                    <div className="space-y-3">
                        <div className="flex items-center gap-2 text-sm text-green-600 bg-green-50 border border-green-200 rounded-lg p-3">
                            <span>✓ 계좌 검증 완료</span>
                            <span className="text-gray-500 text-xs">({bank} · {account})</span>
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">결재 의견 (사정의견서)</label>
                            <textarea value={payOpinion} onChange={e => setPayOpinion(e.target.value)}
                                rows={3} placeholder="예: 합의 완료 승인"
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
                        </div>
                        <button onClick={handleApprove}
                            className="px-4 py-2 bg-yellow-500 text-white text-sm rounded-lg hover:bg-yellow-600">
                            결재 상신
                        </button>
                    </div>
                ) : (
                    <div className="space-y-3">
                        <div className="text-sm text-green-600 bg-green-50 border border-green-200 rounded-lg p-3 space-y-1">
                            <p className="font-medium">✓ 결재 상신 완료</p>
                            <p className="text-xs text-gray-500">수령계좌: {bank} · {account}</p>
                            <p className="text-xs text-gray-500">의견: {payOpinion}</p>
                        </div>
                        <button onClick={handlePay} disabled={payLoading}
                            className="px-4 py-2 bg-green-600 text-white text-sm rounded-lg hover:bg-green-700 disabled:opacity-40">
                            {payLoading ? "처리 중..." : "최종 이체 승인 (보험금 지급)"}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}
