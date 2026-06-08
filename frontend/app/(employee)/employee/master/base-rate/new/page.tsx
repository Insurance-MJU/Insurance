'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createBaseRate } from "@/queries/master";
import { RATE_TYPES } from "@/types/master";

// 요율 종류별 dimension1/dimension2 힌트
const DIM_HINTS: Record<string, { d1: string; d1ph: string; d2: string; d2ph: string }> = {
    VEHICLE_TYPE:    { d1: "차종",        d1ph: "소형 A",             d2: "",         d2ph: "" },
    GENDER_AGE:      { d1: "성별",        d1ph: "남성",               d2: "연령대",    d2ph: "26세~30세" },
    COVERAGE:        { d1: "보험종목",     d1ph: "개인용자동차보험",    d2: "담보",      d2ph: "대물배상(임의)" },
    DRIVER_LIMIT:    { d1: "운전자한정종류", d1ph: "가족",              d2: "",         d2ph: "" },
    DRIVER_AGE_LIMIT:{ d1: "운전자한정종류", d1ph: "미가입",            d2: "연령한정특약", d2ph: "30세이상" },
    INTEREST:        { d1: "구분",        d1ph: "기준금리",            d2: "",         d2ph: "" },
    EXPENSE:         { d1: "구분",        d1ph: "순사업비율",          d2: "",         d2ph: "" },
};

const empty = { rateType: "VEHICLE_TYPE", dimension1: "", dimension2: "", rateValue: "1.0000", effectiveYear: String(new Date().getFullYear()), note: "" };

export default function BaseRateNewPage() {
    const router = useRouter();
    const [form, setForm] = useState(empty);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: any) => setForm(f => ({ ...f, [k]: v }));

    const hint = DIM_HINTS[form.rateType] ?? DIM_HINTS.VEHICLE_TYPE;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true); setError("");
        try {
            await createBaseRate({
                rateType: form.rateType,
                dimension1: form.dimension1,
                dimension2: form.dimension2 || null,
                rateValue: Number(form.rateValue),
                effectiveYear: Number(form.effectiveYear),
                note: form.note || null,
            });
            router.push("/employee/master/base-rate");
        } catch (e: any) { setError(e.message ?? "등록 실패"); }
        finally { setLoading(false); }
    };

    return (
        <div className="max-w-xl">
            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <h1 className="text-xl font-bold text-gray-800">기초율 요율 계수 등록</h1>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}

            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6 grid grid-cols-2 gap-4">
                {/* 종류 */}
                <div className="col-span-2">
                    <label className="block text-xs font-medium text-gray-500 mb-1">요율 종류</label>
                    <select value={form.rateType} onChange={e => { set("rateType", e.target.value); set("dimension1", ""); set("dimension2", ""); }}
                        className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white">
                        {RATE_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select>
                </div>

                {/* dimension1 */}
                <div className={hint.d2 ? "" : "col-span-2"}>
                    <label className="block text-xs font-medium text-gray-500 mb-1">{hint.d1 || "조건"}</label>
                    <input value={form.dimension1} onChange={e => set("dimension1", e.target.value)}
                        placeholder={hint.d1ph}
                        className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500"
                        required />
                </div>

                {/* dimension2 (있는 경우만) */}
                {hint.d2 && (
                    <div>
                        <label className="block text-xs font-medium text-gray-500 mb-1">{hint.d2}</label>
                        <input value={form.dimension2} onChange={e => set("dimension2", e.target.value)}
                            placeholder={hint.d2ph}
                            className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500" />
                    </div>
                )}

                {/* 요율 값 */}
                <div>
                    <label className="block text-xs font-medium text-gray-500 mb-1">요율 계수</label>
                    <input type="number" step="0.0001" value={form.rateValue} onChange={e => set("rateValue", e.target.value)}
                        placeholder="1.0000 = 기준"
                        className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500"
                        required />
                    <p className="text-xs text-gray-400 mt-1">기준=1.000, 위험↑=1.2, 위험↓=0.8</p>
                </div>

                {/* 기준 연도 */}
                <div>
                    <label className="block text-xs font-medium text-gray-500 mb-1">적용 기준 연도</label>
                    <input type="number" value={form.effectiveYear} onChange={e => set("effectiveYear", e.target.value)}
                        className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500"
                        required />
                </div>

                {/* 비고 */}
                <div className="col-span-2">
                    <label className="block text-xs font-medium text-gray-500 mb-1">비고 (선택)</label>
                    <input value={form.note} onChange={e => set("note", e.target.value)}
                        placeholder="예: 2025년 통계 기반 산출"
                        className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500" />
                </div>

                <div className="col-span-2 flex gap-3">
                    <button type="submit" disabled={loading}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "등록 중..." : "등록"}
                    </button>
                    <button type="button" onClick={() => router.back()}
                        className="px-5 py-2 border border-gray-300 text-gray-600 text-sm rounded-lg hover:bg-gray-50">취소</button>
                </div>
            </form>
        </div>
    );
}
