'use client';

import { useEffect, useState } from "react";
import Link from "next/link";
import { getBaseRates, getBaseRateStats, deleteBaseRate } from "@/queries/master";
import { RATE_TYPES, STAT_TYPES } from "@/types/master";

const rateLabel = (v: string) => RATE_TYPES.find(r => r.value === v)?.label ?? v;
const statLabel = (v: string) => STAT_TYPES.find(s => s.value === v)?.label ?? v;

function fmt(n: number) {
    if (n === 0) return "-";
    return new Intl.NumberFormat("ko-KR").format(n);
}

export default function BaseRatePage() {
    const [tab, setTab] = useState<"rate" | "stat">("rate");
    const [rateType, setRateType] = useState("");
    const [rates, setRates] = useState<any[]>([]);
    const [stats, setStats] = useState<any[]>([]);
    const [statType, setStatType] = useState("VEHICLE_TYPE");
    const [error, setError] = useState("");

    const loadRates = async () => {
        try { setRates(await getBaseRates(rateType || undefined)); } catch { setError("요율 로드 실패"); }
    };
    const loadStats = async () => {
        try { setStats(await getBaseRateStats(statType)); } catch { setError("통계 로드 실패"); }
    };

    useEffect(() => { loadRates(); }, [rateType]);
    useEffect(() => { if (tab === "stat") loadStats(); }, [tab, statType]);

    const currentStat = STAT_TYPES.find(s => s.value === statType);

    return (
        <div className="max-w-5xl">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h1 className="text-xl font-bold text-gray-800">기초율</h1>
                    <p className="text-sm text-gray-500">요율 계수 관리 및 통계 원본 확인</p>
                </div>
                {tab === "rate" && (
                    <Link href="/employee/master/base-rate/new"
                        className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                        + 기초율 등록
                    </Link>
                )}
            </div>

            {/* 탭 */}
            <div className="flex gap-1 mb-4 border-b border-gray-200">
                {(["rate", "stat"] as const).map(t => (
                    <button key={t} onClick={() => setTab(t)}
                        className={`px-4 py-2 text-sm font-medium border-b-2 -mb-px transition-colors
                            ${tab === t ? "border-blue-600 text-blue-600" : "border-transparent text-gray-500 hover:text-gray-700"}`}>
                        {t === "rate" ? "요율 계수" : "통계 원본 (CSV)"}
                    </button>
                ))}
            </div>

            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}

            {/* 요율 계수 탭 */}
            {tab === "rate" && (
                <>
                    <div className="mb-4 flex gap-2 flex-wrap">
                        <button onClick={() => setRateType("")}
                            className={`px-3 py-1.5 text-xs rounded-full border ${!rateType ? "bg-blue-600 text-white border-blue-600" : "border-gray-200 text-gray-600 hover:bg-gray-50"}`}>
                            전체
                        </button>
                        {RATE_TYPES.map(t => (
                            <button key={t.value} onClick={() => setRateType(t.value)}
                                className={`px-3 py-1.5 text-xs rounded-full border ${rateType === t.value ? "bg-blue-600 text-white border-blue-600" : "border-gray-200 text-gray-600 hover:bg-gray-50"}`}>
                                {t.label}
                            </button>
                        ))}
                    </div>
                    <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden">
                        <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                            <tr>{["종류", "조건1", "조건2", "요율", "기준연도", "비고", ""].map(h => (
                                <th key={h} className="px-4 py-3 text-left">{h}</th>
                            ))}</tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {rates.length === 0 && (
                                <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400">등록된 기초율이 없습니다</td></tr>
                            )}
                            {rates.map((r: any) => (
                                <tr key={r.id} className="hover:bg-gray-50">
                                    <td className="px-4 py-3">
                                        <span className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded">{rateLabel(r.rateType)}</span>
                                    </td>
                                    <td className="px-4 py-3 font-medium">{r.dimension1}</td>
                                    <td className="px-4 py-3 text-gray-500">{r.dimension2 ?? "-"}</td>
                                    <td className="px-4 py-3 font-mono font-semibold">{r.rateValue.toFixed(4)}</td>
                                    <td className="px-4 py-3 text-gray-500">{r.effectiveYear}</td>
                                    <td className="px-4 py-3 text-gray-400 text-xs max-w-[150px] truncate">{r.note ?? "-"}</td>
                                    <td className="px-4 py-3 text-right">
                                        <button onClick={() => { if (confirm("삭제?")) deleteBaseRate(r.id).then(loadRates); }}
                                            className="text-xs text-red-400 hover:text-red-600">삭제</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </>
            )}

            {/* 통계 원본 탭 */}
            {tab === "stat" && (
                <>
                    <div className="mb-4 flex gap-2 flex-wrap">
                        {STAT_TYPES.map(t => (
                            <button key={t.value} onClick={() => setStatType(t.value)}
                                className={`px-3 py-1.5 text-xs rounded-full border ${statType === t.value ? "bg-indigo-600 text-white border-indigo-600" : "border-gray-200 text-gray-600 hover:bg-gray-50"}`}>
                                {t.label}
                            </button>
                        ))}
                    </div>
                    <p className="text-xs text-gray-400 mb-3">CSV 자동 임포트 데이터 — 손해액 단위: 천 원</p>
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm bg-white border border-gray-200 rounded-xl overflow-hidden min-w-[700px]">
                            <thead className="bg-gray-50 text-xs text-gray-500 font-medium">
                                <tr>
                                    <th className="px-3 py-3 text-left">연도</th>
                                    <th className="px-3 py-3 text-left">{currentStat?.dim1 ?? "구분1"}</th>
                                    {currentStat?.dim2 && <th className="px-3 py-3 text-left">{currentStat.dim2}</th>}
                                    {statType === "DRIVER_LIMIT" && <th className="px-3 py-3 text-left">운전자한정종류</th>}
                                    <th className="px-3 py-3 text-right">손해액</th>
                                    <th className="px-3 py-3 text-right">사망</th>
                                    <th className="px-3 py-3 text-right">부상</th>
                                    <th className="px-3 py-3 text-right">전손</th>
                                    <th className="px-3 py-3 text-right">분손</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {stats.length === 0 && (
                                    <tr><td colSpan={9} className="px-4 py-8 text-center text-gray-400">데이터 없음</td></tr>
                                )}
                                {stats.map((s: any) => (
                                    <tr key={s.id} className="hover:bg-gray-50">
                                        <td className="px-3 py-2 text-gray-500">{s.statYear}</td>
                                        <td className="px-3 py-2 font-medium">{s.dimension1}</td>
                                        {currentStat?.dim2 && <td className="px-3 py-2 text-gray-600">{s.dimension2 || "-"}</td>}
                                        {statType === "DRIVER_LIMIT" && <td className="px-3 py-2 text-gray-600">{s.dimension3 || "-"}</td>}
                                        <td className="px-3 py-2 text-right font-mono text-xs">{fmt(s.lossAmount)}</td>
                                        <td className="px-3 py-2 text-right text-xs">{fmt(s.deathCount)}</td>
                                        <td className="px-3 py-2 text-right text-xs">{fmt(s.injuryCount)}</td>
                                        <td className="px-3 py-2 text-right text-xs">{fmt(s.totalLossCount)}</td>
                                        <td className="px-3 py-2 text-right text-xs">{fmt(s.partialLossCount)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </>
            )}
        </div>
    );
}
