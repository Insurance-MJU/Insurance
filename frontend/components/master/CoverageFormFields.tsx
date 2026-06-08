'use client';

import { useEffect, useState } from "react";
import { COVERAGE_TYPES, LIMIT_TYPES, DEDUCTIBLE_TYPES, LIMIT_UNITS, COMPENSATION_TYPES } from "@/types/master";
import ExclusionListEditor from "./ExclusionListEditor";
import RequiredCoveragesSelector from "./RequiredCoveragesSelector";
import LimitOptionEditor from "./LimitOptionEditor";
import CommonExclusionPicker from "./CommonExclusionPicker";
import { getProvisionItems } from "@/queries/master";

const L = "block text-xs font-medium text-gray-500 mb-1";
const I = "w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white";

interface Props {
    form: any;
    set: (k: string, v: any) => void;
}

const CHECKS = [["mandatory", "의무 가입"], ["autoRestoration", "자동 복원"], ["excessPay", "초과 비용 지급"]] as const;

export default function CoverageFormFields({ form, set }: Props) {
    const showLimitAmount = form.limitType === "FIXED" && form.limitOptions.length === 0;
    // VEHICLE_VALUE: 사고 시점 시가로 결정 → 마스터에 금액 입력 불필요
    const limitAmountLabel = form.mandatory ? "법정기준한도 (원)" : "기본 가입한도 (원)";
    const [provisionItems, setProvisionItems] = useState<any[]>([]);
    useEffect(() => { getProvisionItems().then(setProvisionItems).catch(() => {}); }, []);

    const addCommonExclusions = (picks: any[]) => {
        const items = picks.map(e => ({
            name: e.name,
            description: e.description ?? "",
            subItems: (e.subItems ?? []).map((s: any) => ({ label: s.label ?? "", content: s.content, isException: s.isException })),
        }));
        set("exclusions", [...form.exclusions, ...items]);
    };

    return (
        <div className="space-y-5">
            <div className="grid grid-cols-2 gap-4">
                <div><label className={L}>담보 종류</label>
                    <select value={form.coverageType} onChange={e => set("coverageType", e.target.value)} className={I}>
                        {COVERAGE_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select></div>
                <div><label className={L}>담보명</label>
                    <input value={form.name} onChange={e => set("name", e.target.value)} className={I} placeholder="예: 대인배상I" /></div>
                <div className="col-span-2"><label className={L}>보장 내용</label>
                    <textarea value={form.description} onChange={e => set("description", e.target.value)} className={`${I} min-h-[120px] resize-y`} /></div>
                <div className="col-span-2"><label className={L}>근거 약관 조항</label>
                    <select value={form.provisionId ?? ""} onChange={e => set("provisionId", e.target.value ? Number(e.target.value) : null)} className={I}>
                        <option value="">— 선택 안 함 —</option>
                        {provisionItems.map((item: any) => (
                            <option key={item.id} value={item.id}>
                                {[item.label, item.title].filter(Boolean).join(" ")}
                                {item.levelTypeDisplayName ? ` (${item.levelTypeDisplayName})` : ""}
                            </option>
                        ))}
                    </select></div>
                <div><label className={L}>한도 유형</label>
                    <select value={form.limitType} onChange={e => set("limitType", e.target.value)} className={I}>
                        {LIMIT_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select></div>
                {showLimitAmount && <div><label className={L}>{limitAmountLabel}</label>
                    <input type="number" value={form.limitAmount} onChange={e => set("limitAmount", e.target.value)} className={I} /></div>}
                <div><label className={L}>자기부담금</label>
                    <select value={form.deductibleType} onChange={e => set("deductibleType", e.target.value)} className={I}>
                        {DEDUCTIBLE_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select></div>
                {form.deductibleType === "FIXED_AMOUNT" && <div><label className={L}>금액 (원)</label>
                    <input type="number" value={form.deductibleAmount} onChange={e => set("deductibleAmount", e.target.value)} className={I} /></div>}
                {form.deductibleType === "RATE" && <div><label className={L}>비율 (0~1)</label>
                    <input type="number" step="0.01" value={form.deductibleRate} onChange={e => set("deductibleRate", e.target.value)} className={I} /></div>}
                <div><label className={L}>한도 단위</label>
                    <select value={form.limitUnit} onChange={e => set("limitUnit", e.target.value)} className={I}>
                        {LIMIT_UNITS.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select></div>
                <div><label className={L}>보상 방식</label>
                    <select value={form.compensationType} onChange={e => set("compensationType", e.target.value)} className={I}>
                        {COMPENSATION_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select></div>
                <div className="col-span-2 flex flex-wrap gap-5">
                    {CHECKS.map(([k, label]) => (
                        <label key={k} className="flex items-center gap-1.5 text-sm text-gray-600 cursor-pointer">
                            <input type="checkbox" checked={form[k]} onChange={e => set(k, e.target.checked)} className="accent-blue-600" />
                            {label}
                        </label>
                    ))}
                </div>
            </div>

            <div><label className={`${L} mb-2`}>가입금액 옵션</label>
                <LimitOptionEditor items={form.limitOptions} onChange={v => set("limitOptions", v)} /></div>

            <div>
                <div className="flex items-center justify-between mb-2">
                    <label className={L}>담보별 면책사항</label>
                    <CommonExclusionPicker onPick={addCommonExclusions} existingNames={form.exclusions.map((e: any) => e.name)} />
                </div>
                <ExclusionListEditor items={form.exclusions} onChange={v => set("exclusions", v)} />
            </div>

            {form.coverageType === "UNINSURED_MOTORIST" && (
                <div><label className={`${L} mb-2`}>선행 가입 필수 담보</label>
                    <RequiredCoveragesSelector selected={form.requiredCoverages} currentType={form.coverageType} onChange={v => set("requiredCoverages", v)} /></div>
            )}
        </div>
    );
}
