'use client';

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { fetchApi } from "@/queries/api";
import { approveContract, rejectContract, supplementContract } from "@/queries/contracts";

type Phase = 'init' | 'analyzing' | 'analyzed' | 'decided';

const RISK_GRADE_COLOR: Record<number, string> = {
  1: "text-green-600 bg-green-50",
  2: "text-blue-600 bg-blue-50",
  3: "text-yellow-600 bg-yellow-50",
  4: "text-orange-600 bg-orange-50",
  5: "text-red-600 bg-red-50",
};

export default function PendingDetailPage() {
  const { no } = useParams();
  const [sub, setSub]           = useState<any>(null);
  const [phase, setPhase]       = useState<Phase>('init');
  const [riskResult, setRisk]   = useState<any>(null);
  const [decision, setDecision] = useState<'APPROVED' | 'REJECTED' | 'SUPPLEMENT' | null>(null);
  const [reason, setReason]     = useState("");
  const [loading, setLoading]   = useState(false);

  useEffect(() => {
    if (!no) return;
    fetchApi(`/subscriptions/${no}`)
      .then(r => setSub(r.data ?? r))
      .catch(() => {});
  }, [no]);

  const handleAnalyze = async () => {
    setPhase('analyzing');
    setLoading(true);
    try {
      const res = await fetchApi(`/subscriptions/${no}/risk-analysis`, { method: "POST" });
      setRisk(res.data ?? res);
      setPhase('analyzed');
    } catch (e: any) {
      alert(e?.message ?? "위험성 분석 실패");
      setPhase('init');
    } finally {
      setLoading(false);
    }
  };

  const handleDecide = async () => {
    if (!decision) return;
    if ((decision === 'REJECTED' || decision === 'SUPPLEMENT') && !reason.trim()) {
      alert("사유를 입력하세요.");
      return;
    }
    setLoading(true);
    try {
      if (decision === 'APPROVED') {
        await approveContract(String(no));
        alert("인수가 승인되었습니다.");
      } else if (decision === 'REJECTED') {
        await rejectContract(String(no), reason);
        alert("인수를 거절했습니다.");
      } else {
        await supplementContract(String(no), reason);
        alert("서류 보완을 요청했습니다.");
      }
      setPhase('decided');
    } catch (e: any) {
      alert(e?.message ?? "처리 실패");
    } finally {
      setLoading(false);
    }
  };

  if (!sub) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;

  const creditInfo = riskResult?.creditInfo;
  const gradeColor = RISK_GRADE_COLOR[riskResult?.riskGrade] ?? "text-gray-600 bg-gray-50";

  return (
    <div className="max-w-3xl space-y-6">
      <div>
        <Link href="/employee/contracts/pending" className="text-sm text-gray-400 hover:text-gray-600">← 청약 심사 목록</Link>
        <h1 className="text-xl font-bold text-gray-800 mt-1">계약인수 심사 (UW-01)</h1>
        <p className="text-xs text-gray-400">{sub.subscriptionNo}</p>
      </div>

      {/* 청약 정보 */}
      <div className="bg-white border border-gray-200 rounded-xl p-5">
        <h2 className="text-sm font-semibold text-gray-600 mb-3">청약 정보</h2>
        <div className="grid grid-cols-2 gap-2 text-sm">
          {[
            ["청약자", sub.applicantName],
            ["상품명", sub.productName],
            ["보험료", `${(sub.premium ?? 0).toLocaleString()} KRW`],
            ["청약일", sub.subscriptionDate],
            ["상태", sub.status],
          ].map(([label, value]) => (
            <div key={label} className="flex gap-2">
              <span className="w-20 text-gray-400 shrink-0">{label}</span>
              <span className="text-gray-800">{value}</span>
            </div>
          ))}
        </div>
      </div>

      {/* 위험성 분석 (UW-02) */}
      <div className="bg-white border border-gray-200 rounded-xl p-5">
        <h2 className="text-sm font-semibold text-gray-600 mb-4">위험성 분석 (UW-02)</h2>

        {phase === 'init' && (
          <div className="text-center py-4">
            <p className="text-sm text-gray-500 mb-4">신용정보원 조회를 통해 위험 등급을 산출합니다.</p>
            <button onClick={handleAnalyze} disabled={loading}
              className="px-6 py-2.5 bg-blue-600 text-white text-sm font-semibold rounded-lg hover:bg-blue-700 disabled:opacity-40">
              위험성 분석 실행
            </button>
          </div>
        )}

        {phase === 'analyzing' && (
          <p className="text-sm text-blue-500 text-center py-4">신용정보원 조회 중...</p>
        )}

        {(phase === 'analyzed' || phase === 'decided') && riskResult && (
          <div className="space-y-4">
            {/* 위험 등급 */}
            <div className={`inline-flex items-center gap-3 px-4 py-3 rounded-xl ${gradeColor}`}>
              <span className="text-2xl font-bold">{riskResult.riskGrade}등급</span>
              <div>
                <p className="text-sm font-semibold">{riskResult.riskGradeLabel}</p>
                <p className="text-xs">위험점수 {riskResult.riskScore?.toFixed(1)}점 · 할증율 +{((riskResult.surchargeRate ?? 0) * 100).toFixed(0)}%</p>
              </div>
            </div>

            {/* 할증 보험료 */}
            {riskResult.surchargeAmount > 0 && (
              <div className="text-sm text-gray-600 bg-gray-50 rounded-lg p-3">
                기본 보험료 + 위험 할증: <span className="font-semibold">{riskResult.surchargeAmount.toLocaleString()}원</span> 추가
              </div>
            )}

            {/* 신용정보 상세 */}
            {creditInfo && !creditInfo.newApplicant && (
              <div className="text-sm space-y-1 border-t pt-3">
                <p className="text-xs text-gray-400 font-medium mb-2">신용정보원 조회 결과</p>
                <div className="grid grid-cols-2 gap-1">
                  {[
                    ["사고건수", `${creditInfo.accidentCount}건`],
                    ["운전경력", `${creditInfo.drivingExperienceYears}년`],
                    ["신용등급", `${creditInfo.creditGrade}등급`],
                    ["보험사기", creditInfo.fraudHistory ? "이력있음" : "해당없음"],
                  ].map(([k, v]) => (
                    <div key={k} className="flex gap-2 text-gray-600">
                      <span className="text-gray-400 w-16 shrink-0">{k}</span>
                      <span>{v}</span>
                    </div>
                  ))}
                </div>
                {creditInfo.accidentHistory?.length > 0 && (
                  <div className="mt-2">
                    <p className="text-xs text-gray-400 mb-1">최근 사고 이력</p>
                    {creditInfo.accidentHistory.map((a: any, i: number) => (
                      <p key={i} className="text-xs text-gray-600">
                        {a.date} · {a.description} · {a.amount?.toLocaleString()}원
                      </p>
                    ))}
                  </div>
                )}
              </div>
            )}
            {creditInfo?.newApplicant && (
              <p className="text-xs text-gray-400">신용정보원 조회 이력 없음 → 기본 위험등급(3등급) 적용</p>
            )}
          </div>
        )}
      </div>

      {/* 심사 결정 */}
      {phase === 'analyzed' && (
        <div className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-gray-600 mb-4">심사 결정</h2>
          <div className="flex gap-3 mb-4">
            {([
              { key: 'APPROVED',   label: '인수 승인', color: 'bg-green-600 hover:bg-green-700' },
              { key: 'SUPPLEMENT', label: '서류보완 요청', color: 'bg-yellow-500 hover:bg-yellow-600' },
              { key: 'REJECTED',   label: '인수 거절', color: 'bg-red-500 hover:bg-red-600' },
            ] as const).map(btn => (
              <button key={btn.key}
                onClick={() => setDecision(btn.key)}
                className={`px-4 py-2 text-sm text-white font-medium rounded-lg transition
                  ${decision === btn.key ? btn.color + ' ring-2 ring-offset-2 ring-current' : 'bg-gray-200 text-gray-600 hover:bg-gray-300'}`}>
                {btn.label}
              </button>
            ))}
          </div>

          {(decision === 'REJECTED' || decision === 'SUPPLEMENT') && (
            <div className="mb-4">
              <label className="block text-xs text-gray-500 mb-1">
                {decision === 'REJECTED' ? '거절 사유' : '보완 요청 내용'} *
              </label>
              <textarea value={reason} onChange={e => setReason(e.target.value)} rows={3}
                placeholder="사유를 입력하세요"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
            </div>
          )}

          {decision && (
            <button onClick={handleDecide} disabled={loading}
              className="px-6 py-2.5 bg-blue-600 text-white text-sm font-semibold rounded-lg hover:bg-blue-700 disabled:opacity-40">
              {loading ? "처리 중..." : "확정"}
            </button>
          )}
        </div>
      )}

      {phase === 'decided' && (
        <div className="bg-green-50 border border-green-200 rounded-xl p-5 text-center">
          <p className="text-green-700 font-semibold">심사가 완료되었습니다.</p>
          <Link href="/employee/contracts/pending" className="mt-3 inline-block text-sm text-blue-500 underline">
            목록으로 돌아가기
          </Link>
        </div>
      )}
    </div>
  );
}
