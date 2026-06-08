'use client';

import { useEffect, useState } from "react";
import { RIDER_TYPES } from "@/types/master";
import ExclusionListEditor from "./ExclusionListEditor";
import CommonExclusionPicker from "./CommonExclusionPicker";
import { getProvisionItems } from "@/queries/master";

const L = "block text-xs font-medium text-gray-500 mb-1";
const I = "w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white";

interface Props {
    form: any;
    set: (k: string, v: any) => void;
}

export default function RiderFormFields({ form, set }: Props) {
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
                <div>
                    <label className={L}>특약 코드</label>
                    <input value={form.riderCode} onChange={e => set("riderCode", e.target.value)}
                        className={I} placeholder="예: BLACKBOX_DISCOUNT" />
                </div>
                <div>
                    <label className={L}>특약명</label>
                    <input value={form.name} onChange={e => set("name", e.target.value)}
                        className={I} placeholder="예: 블랙박스 할인 특약" />
                </div>
                <div className="col-span-2">
                    <label className={L}>설명</label>
                    <textarea value={form.description} onChange={e => set("description", e.target.value)}
                        className={`${I} min-h-[80px] resize-y`} />
                </div>

                <div>
                    <label className={L}>특약 유형</label>
                    <select value={form.riderType} onChange={e => set("riderType", e.target.value)} className={I}>
                        {RIDER_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                    </select>
                </div>

                {form.riderType === "DISCOUNT" && (
                    <div>
                        <label className={L}>할인율 (0 ~ 1, 예: 0.05 = 5%)</label>
                        <input type="number" step="0.0001" min="0" max="1"
                            value={form.discountRate ?? ""}
                            onChange={e => set("discountRate", e.target.value === "" ? null : Number(e.target.value))}
                            className={I} placeholder="0.0500" />
                    </div>
                )}

                <div className="col-span-2">
                    <label className={L}>근거 약관 조항</label>
                    <select value={form.provisionId ?? ""} onChange={e => set("provisionId", e.target.value ? Number(e.target.value) : null)} className={I}>
                        <option value="">— 선택 안 함 —</option>
                        {provisionItems.map((item: any) => (
                            <option key={item.id} value={item.id}>
                                {[item.label, item.title].filter(Boolean).join(" ")}
                                {item.levelTypeDisplayName ? ` (${item.levelTypeDisplayName})` : ""}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="col-span-2">
                    <label className="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
                        <input type="checkbox" checked={form.mandatory} onChange={e => set("mandatory", e.target.checked)} className="accent-blue-600" />
                        의무 부가
                    </label>
                </div>
            </div>

            <div>
                <div className="flex items-center justify-between mb-2">
                    <label className={L}>특약 면책사항</label>
                    <CommonExclusionPicker onPick={addCommonExclusions} existingNames={form.exclusions.map((e: any) => e.name)} />
                </div>
                <ExclusionListEditor items={form.exclusions} onChange={v => set("exclusions", v)} />
            </div>
        </div>
    );
}
