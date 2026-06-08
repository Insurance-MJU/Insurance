'use client';

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { fetchApi } from "@/queries/api";

export default function AccidentDetailPage() {
    const { id } = useParams();
    const [accident, setAccident]     = useState<any>(null);
    const [investigation, setInvestigation] = useState<any>(null);
    const [investigators, setInvestigators] = useState<any[]>([]);
    const [empId, setEmpId]           = useState("");
    const [assignLoading, setAssignLoading] = useState(false);
    const [assignedClaim, setAssignedClaim] = useState<any>(null);

    useEffect(() => {
        if (!id) return;
        fetchApi(`/accidents/${id}`).then(r => setAccident(r.data ?? r)).catch(() => {});
        fetchApi(`/accidents/${id}/investigation`).then(r => setInvestigation(r.data ?? r)).catch(() => {});
        fetchApi(`/investigators`).then(r => setInvestigators(Array.isArray(r) ? r : r?.data ?? [])).catch(() => {});
        fetchApi(`/accidents/${id}/claim`).then(r => {
            const data = r.data ?? r;
            if (data?.claimId) setAssignedClaim(data);
        }).catch(() => {});
    }, [id]);

    const handleAssign = async () => {
        if (!empId.trim()) { alert("직원 번호를 입력하세요."); return; }
        setAssignLoading(true);
        try {
            const res = await fetchApi(`/accidents/${id}/assign`, {
                method: "PUT",
                body: JSON.stringify({ employeeId: empId }),
            });
            setAssignedClaim(res.data ?? res);
            alert("담당자가 배당되었습니다.");
        } catch (e: any) { alert(e?.message ?? "배당 실패"); }
        finally { setAssignLoading(false); }
    };

    if (!accident) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;

    return (
        <div className="max-w-3xl space-y-6">
            <div>
                <Link href="/employee/accidents" className="text-sm text-gray-400 hover:text-gray-600">← 사고 목록</Link>
                <h1 className="text-xl font-bold text-gray-800 mt-1">사고 상세</h1>
                <p className="text-xs text-gray-400">{accident.accidentId}</p>
            </div>

            {/* 사고 정보 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <h2 className="text-sm font-semibold text-gray-600 mb-3">사고 기본 정보</h2>
                <div className="grid grid-cols-2 gap-2 text-sm">
                    {[
                        ["신고자",    accident.reportedBy],
                        ["연락처",    accident.phone],
                        ["사고일",    accident.accidentDate],
                        ["사고장소",  accident.accidentLocation],
                        ["계약번호",  accident.contractId],
                        ["상태",      accident.status],
                    ].map(([label, value]) => (
                        <div key={label} className="flex gap-3">
                            <span className="w-20 text-gray-400 shrink-0">{label}</span>
                            <span className="text-gray-800">{value}</span>
                        </div>
                    ))}
                </div>
            </div>

            {/* CL-01: 현장조사역 배당 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <h2 className="text-sm font-semibold text-gray-600 mb-4">현장조사역 배당 (CL-01)</h2>
                {accident.status === '접수 완료' || accident.status === '미처리' ? (
                    <>
                        {investigators.length > 0 && (
                            <div className="mb-3 flex flex-wrap gap-2">
                                <span className="text-xs text-gray-400">배당 가능 직원:</span>
                                {investigators.map((inv: any) => (
                                    <button key={inv.employeeId} onClick={() => setEmpId(inv.employeeId)}
                                        className="text-xs px-2 py-0.5 bg-gray-100 rounded hover:bg-blue-50 text-gray-700">
                                        {inv.employeeId} ({inv.name ?? "직원"})
                                    </button>
                                ))}
                            </div>
                        )}
                        <div className="flex gap-2">
                            <input value={empId} onChange={e => setEmpId(e.target.value)}
                                placeholder="직원 번호 (예: EMP-1023)"
                                className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                            <button onClick={handleAssign} disabled={assignLoading}
                                className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-40">
                                {assignLoading ? "배당 중..." : "배당 및 접수 확정"}
                            </button>
                        </div>
                        {assignedClaim && (
                            <p className="mt-2 text-xs text-green-600">
                                클레임 {assignedClaim.claimId} 생성 →{" "}
                                <Link href={`/employee/claims/${assignedClaim.claimId}`} className="underline">손해 처리 이동</Link>
                            </p>
                        )}
                    </>
                ) : (
                    <div className="space-y-3">
                        <p className="text-sm text-green-600 font-medium">✓ 배당 완료</p>
                        {assignedClaim && (
                            <div className="text-sm text-gray-600 bg-gray-50 rounded-lg p-3 space-y-1">
                                <p>담당자: <span className="font-medium">{assignedClaim.assignedEmployee ?? '—'}</span></p>
                                <p>클레임 번호: <span className="font-medium">{assignedClaim.claimId}</span></p>
                            </div>
                        )}
                        {assignedClaim?.claimId && (
                            <Link href={`/employee/claims/${assignedClaim.claimId}`}
                                className="inline-block px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700">
                                다음 단계 (손해 처리) →
                            </Link>
                        )}
                    </div>
                )}
            </div>

            {/* CL-03: 손해 조사 */}
            <div className="bg-white border border-gray-200 rounded-xl p-5">
                <div className="flex items-center justify-between mb-3">
                    <div>
                        <h2 className="text-sm font-semibold text-gray-600">손해 조사 (CL-03)</h2>
                        <p className="text-xs text-gray-400 mt-0.5">현장 조사 결과 및 과실비율을 등록합니다.</p>
                    </div>
                    <Link href={`/employee/accidents/${id}/investigation`}
                        className="px-4 py-2 bg-green-600 text-white text-sm font-medium rounded-lg hover:bg-green-700">
                        손해 조사 →
                    </Link>
                </div>
                {investigation?.exists ? (
                    <div className="text-sm space-y-1 text-gray-600 bg-green-50 rounded-lg p-3">
                        <p className="text-green-700 font-medium">✓ 조사 완료 ({investigation.savedAt})</p>
                        <p>손해코드: {investigation.damageCode} · 면/부책: {investigation.liability}</p>
                    </div>
                ) : (
                    <p className="text-sm text-gray-400">아직 조사 내역이 없습니다.</p>
                )}
            </div>
        </div>
    );
}
