'use client';

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { fetchApi } from "@/queries/api";
import { approveContract, rejectContract, supplementContract } from "@/queries/contracts";

type Phase = 'init' | 'inquiring' | 'inquired' | 'graded' | 'detail_open' | 'decided';

const INQUIRY_ITEMS = [
  { key: 'accident',  label: '최근 3년 사고이력' },
  { key: 'driving',   label: '운전경력' },
  { key: 'credit',    label: '신용등급' },
  { key: 'fraud',     label: '보험사기 의심이력' },
] as const;

const GRADE_CRITERIA = [
  { grade: 1, range: '0.0 ~ 0.5',  label: '매우 우량', color: 'text-green-600' },
  { grade: 2, range: '0.6 ~ 1.0',  label: '우량',     color: 'text-blue-600' },
  { grade: 3, range: '1.1 ~ 2.0',  label: '보통',     color: 'text-yellow-600' },
  { grade: 4, range: '2.1 ~ 3.5',  label: '주의',     color: 'text-orange-600' },
  { grade: 5, range: '3.6 이상',   label: '고위험',   color: 'text-red-600' },
];

const GRADE_COLOR: Record<number, string> = {
  1: 'text-green-600 bg-green-50 border-green-200',
  2: 'text-blue-600 bg-blue-50 border-blue-200',
  3: 'text-yellow-600 bg-yellow-50 border-yellow-200',
  4: 'text-orange-600 bg-orange-50 border-orange-200',
  5: 'text-red-600 bg-red-50 border-red-200',
};

export default function PendingDetailPage() {
  const { no } = useParams();
  const [sub, setSub]               = useState<any>(null);
  const [phase, setPhase]           = useState<Phase>('init');
  const [checkedItems, setChecked]  = useState<Set<string>>(new Set(INQUIRY_ITEMS.map(i => i.key)));
  const [riskResult, setRisk]       = useState<any>(null);
  const [decision, setDecision]     = useState<'APPROVED' | 'REJECTED' | 'SUPPLEMENT' | null>(null);
  const [reason, setReason]         = useState("");
  const [loading, setLoading]       = useState(false);
  const [analysisConfirmed, setAnalysisConfirmed] = useState(false);

  useEffect(() => {
    if (!no) return;
    fetchApi(`/subscriptions/${no}`)
      .then(r => setSub(r.data ?? r))
      .catch(() => {});
  }, [no]);

  const maskedSsn = (ssn: string) => ssn ? ssn.slice(0, 7) + '******' : '-';

  const handleInquire = async () => {
    setPhase('inquiring');
    setLoading(true);
    try {
      const res = await fetchApi(`/subscriptions/${no}/risk-analysis`, { method: "POST" });
      setRisk(res.data ?? res);
      setPhase('inquired');
    } catch (e: any) {
      alert(e?.message ?? "신용정보 조회 실패");
      setPhase('init');
    } finally {
      setLoading(false);
    }
  };

  const handleGrade = () => setPhase('graded');

  const handleConfirmAnalysis = () => {
    setAnalysisConfirmed(true);
    setPhase('graded');
    alert("위험 분석 데이터가 성공적으로 반영되었습니다.");
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

  const toggleItem = (key: string) => {
    setChecked(prev => {
      const next = new Set(prev);
      next.has(key) ? next.delete(key) : next.add(key);
      return next;
    });
  };

  if (!sub) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;

  const credit    = riskResult?.creditInfo;
  const gradeColor = GRADE_COLOR[riskResult?.riskGrade] ?? 'text-gray-600 bg-gray-50 border-gray-200';

  // 항목별 감점 요인 계산 (상세보기용)
  const deductions = riskResult ? [
    credit?.accidentCount > 0 && { label: `사고 건수: ${credit.accidentCount}건`, score: (credit.accidentCount * 1.2).toFixed(1) },
    credit?.drivingExperienceYears < 3 && { label: `운전경력 부족: ${credit.drivingExperienceYears}년`, score: ((3 - credit.drivingExperienceYears) * 0.15).toFixed(1) },
    credit?.creditGrade > 3 && { label: `신용등급: ${credit.creditGrade}등급`, score: ((credit.creditGrade - 3) * 0.1).toFixed(1) },
    credit?.fraudHistory && { label: '보험사기 의심이력', score: '2.0' },
  ].filter(Boolean) : [];

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
            ["상태",   sub.status],
          ].map(([label, value]) => (
            <div key={label} className="flex gap-2">
              <span className="w-20 text-gray-400 shrink-0">{label}</span>
              <span className="text-gray-800">{value}</span>
            </div>
          ))}
        </div>
      </div>

      {/* UW-02: 신용정보 조회 */}
      <div className="bg-white border border-gray-200 rounded-xl p-5 space-y-4">
        <h2 className="text-sm font-semibold text-gray-600">신용정보 조회 (UW-02)</h2>

        {/* 조회 대상자 정보 */}
        <div className="bg-gray-50 rounded-lg p-3 text-sm space-y-1">
          <p className="text-xs text-gray-400 font-medium mb-2">조회 대상자</p>
          <div className="grid grid-cols-3 gap-2">
            <div><span className="text-gray-400">성명</span> <span className="ml-2 font-medium">{sub.applicantName}</span></div>
            <div><span className="text-gray-400">주민번호</span> <span className="ml-2 font-mono">{maskedSsn(sub.ssn ?? '')}</span></div>
            <div><span className="text-gray-400">차량번호</span> <span className="ml-2">{sub.carNumber ?? '-'}</span></div>
          </div>
        </div>

        {/* 조회 항목 선택 */}
        {phase === 'init' && (
          <>
            <div>
              <p className="text-xs text-gray-400 font-medium mb-2">조회 항목</p>
              <div className="grid grid-cols-2 gap-2">
                {INQUIRY_ITEMS.map(item => (
                  <label key={item.key} className="flex items-center gap-2 text-sm cursor-pointer">
                    <input
                      type="checkbox"
                      checked={checkedItems.has(item.key)}
                      onChange={() => toggleItem(item.key)}
                      className="w-4 h-4 accent-blue-600"
                    />
                    {item.label}
                  </label>
                ))}
              </div>
            </div>
            <button onClick={handleInquire} disabled={checkedItems.size === 0}
              className="px-5 py-2 bg-blue-600 text-white text-sm font-semibold rounded-lg hover:bg-blue-700 disabled:opacity-40">
              조회
            </button>
          </>
        )}

        {phase === 'inquiring' && (
          <p className="text-sm text-blue-500 py-2">신용정보원 조회 중...</p>
        )}

        {/* 조회 결과 */}
        {(phase === 'inquired' || phase === 'graded' || phase === 'detail_open') && credit && (
          <div className="space-y-3">
            <p className="text-xs text-gray-400 font-medium">조회 결과</p>

            {checkedItems.has('accident') && (
              <div className="border rounded-lg p-3 text-sm">
                <p className="font-medium text-gray-700 mb-1">최근 3년 사고이력</p>
                {credit.newApplicant || !credit.accidentHistory?.length ? (
                  <p className="text-gray-400 text-xs">사고이력 없음</p>
                ) : (
                  credit.accidentHistory.map((a: any, i: number) => (
                    <p key={i} className="text-xs text-gray-600">{a.date} · {a.description} · {a.amount?.toLocaleString()}원</p>
                  ))
                )}
              </div>
            )}
            {checkedItems.has('driving') && (
              <div className="border rounded-lg p-3 text-sm">
                <p className="font-medium text-gray-700 mb-1">운전경력</p>
                <p className="text-xs text-gray-600">보험 가입 경력 {credit.drivingExperienceYears}년</p>
              </div>
            )}
            {checkedItems.has('credit') && (
              <div className="border rounded-lg p-3 text-sm">
                <p className="font-medium text-gray-700 mb-1">신용등급</p>
                <p className="text-xs text-gray-600">NICE: {credit.creditGrade}등급</p>
              </div>
            )}
            {checkedItems.has('fraud') && (
              <div className="border rounded-lg p-3 text-sm">
                <p className="font-medium text-gray-700 mb-1">보험사기 의심이력</p>
                <p className="text-xs text-gray-600">{credit.fraudHistory ? '이력 있음' : '해당 없음'}</p>
              </div>
            )}

            {phase === 'inquired' && (
              <button onClick={handleGrade}
                className="px-5 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700">
                위험등급 산출
              </button>
            )}
          </div>
        )}

        {/* 위험등급 결과 */}
        {(phase === 'graded' || phase === 'detail_open') && riskResult && (
          <div className="space-y-3 border-t pt-4">
            <p className="text-xs text-gray-400 font-medium">위험등급 산출 결과</p>
            <button
              onClick={() => setPhase('detail_open')}
              className={`inline-flex items-center gap-4 px-4 py-3 rounded-xl border cursor-pointer hover:brightness-95 transition ${gradeColor}`}
              title="클릭하여 상세 내역 보기"
            >
              <span className="text-3xl font-bold">{riskResult.riskGrade}등급</span>
              <div className="text-left">
                <p className="text-sm font-semibold">{riskResult.riskGradeLabel}</p>
                <p className="text-xs">위험점수 {riskResult.riskScore?.toFixed(1)}점 · 할증율 +{((riskResult.surchargeRate ?? 0) * 100).toFixed(0)}%</p>
                {riskResult.surchargeAmount > 0 && (
                  <p className="text-xs mt-0.5">할증 보험료: +{riskResult.surchargeAmount.toLocaleString()}원</p>
                )}
                <p className="text-xs mt-1 underline opacity-60">상세 내역 보기 →</p>
              </div>
            </button>
            {analysisConfirmed && (
              <p className="text-xs text-green-600 font-medium">✓ 위험 분석 데이터가 성공적으로 반영되었습니다.</p>
            )}
          </div>
        )}
      </div>

      {/* 상세보기 팝업 */}
      {phase === 'detail_open' && riskResult && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg mx-4 p-6 space-y-5">
            <h3 className="text-base font-bold text-gray-800">위험등급 산출 상세 내역</h3>

            {/* 항목별 감점 */}
            <div>
              <p className="text-xs font-semibold text-gray-500 mb-2">항목별 감점 요인</p>
              <table className="w-full text-sm">
                <thead className="bg-gray-50 text-xs text-gray-400">
                  <tr>
                    <th className="px-3 py-2 text-left">항목</th>
                    <th className="px-3 py-2 text-right">점수</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {deductions.length === 0 ? (
                    <tr><td colSpan={2} className="px-3 py-3 text-center text-gray-400 text-xs">감점 요인 없음</td></tr>
                  ) : (
                    (deductions as any[]).map((d, i) => (
                      <tr key={i}>
                        <td className="px-3 py-2 text-gray-700">{d.label}</td>
                        <td className="px-3 py-2 text-right font-mono text-red-500">+{d.score}</td>
                      </tr>
                    ))
                  )}
                  <tr className="bg-gray-50 font-semibold">
                    <td className="px-3 py-2">총점</td>
                    <td className="px-3 py-2 text-right font-mono">{riskResult.riskScore?.toFixed(1)}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            {/* 등급 판정 기준표 */}
            <div>
              <p className="text-xs font-semibold text-gray-500 mb-2">등급 판정 기준표</p>
              <table className="w-full text-sm">
                <thead className="bg-gray-50 text-xs text-gray-400">
                  <tr>
                    <th className="px-3 py-2 text-left">등급</th>
                    <th className="px-3 py-2 text-left">위험점수</th>
                    <th className="px-3 py-2 text-left">판정</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {GRADE_CRITERIA.map(g => (
                    <tr key={g.grade} className={g.grade === riskResult.riskGrade ? 'bg-yellow-50' : ''}>
                      <td className={`px-3 py-2 font-semibold ${g.color}`}>{g.grade}등급 {g.grade === riskResult.riskGrade ? '◀' : ''}</td>
                      <td className="px-3 py-2 font-mono text-xs text-gray-500">{g.range}</td>
                      <td className={`px-3 py-2 ${g.color}`}>{g.label}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <button onClick={handleConfirmAnalysis}
              className="w-full py-2.5 bg-blue-600 text-white text-sm font-semibold rounded-lg hover:bg-blue-700">
              분석 결과 확정
            </button>
          </div>
        </div>
      )}

      {/* 심사 결정 */}
      {(phase === 'graded' || phase === 'detail_open') && phase !== 'decided' && (
        <div className="bg-white border border-gray-200 rounded-xl p-5">
          <h2 className="text-sm font-semibold text-gray-600 mb-4">심사 결정</h2>
          <div className="flex gap-3 mb-4">
            {([
              { key: 'APPROVED',   label: '인수 승인',    color: 'bg-green-600 hover:bg-green-700' },
              { key: 'SUPPLEMENT', label: '서류보완 요청', color: 'bg-yellow-500 hover:bg-yellow-600' },
              { key: 'REJECTED',   label: '인수 거절',    color: 'bg-red-500 hover:bg-red-600' },
            ] as const).map(btn => (
              <button key={btn.key}
                onClick={() => setDecision(btn.key)}
                className={`px-4 py-2 text-sm text-white font-medium rounded-lg transition
                  ${btn.color} ${decision === btn.key ? 'ring-2 ring-offset-2 ring-white ring-inset brightness-110' : 'opacity-70 hover:opacity-100'}`}>
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
          <Link href="/employee/contracts/pending"
            className="mt-3 inline-block text-sm text-blue-500 underline">
            목록으로 돌아가기
          </Link>
        </div>
      )}
    </div>
  );
}
