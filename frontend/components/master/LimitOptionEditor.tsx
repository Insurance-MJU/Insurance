'use client';

import { LIMIT_DETAIL_TYPES } from "@/types/master";

export type DetailItem = { detailType: string; amount: string };
export type LimitOptionItem = { optionName: string; isDefault: boolean; details: DetailItem[] };

interface Props {
    items: LimitOptionItem[];
    onChange: (items: LimitOptionItem[]) => void;
}

const emptyDetail = (): DetailItem => ({ detailType: "TOTAL", amount: "" });
const emptyOption = (): LimitOptionItem => ({ optionName: "", isDefault: false, details: [emptyDetail()] });

export default function LimitOptionEditor({ items, onChange }: Props) {
    const addOption = () => onChange([...items, emptyOption()]);
    const removeOption = (i: number) => onChange(items.filter((_, idx) => idx !== i));
    const updateOption = (i: number, key: keyof LimitOptionItem, val: any) => {
        const next = [...items];
        next[i] = { ...next[i], [key]: val };
        onChange(next);
    };

    const addDetail = (i: number) => updateOption(i, "details", [...items[i].details, emptyDetail()]);
    const removeDetail = (i: number, j: number) =>
        updateOption(i, "details", items[i].details.filter((_, idx) => idx !== j));
    const updateDetail = (i: number, j: number, key: keyof DetailItem, val: string) => {
        const details = [...items[i].details];
        details[j] = { ...details[j], [key]: val };
        updateOption(i, "details", details);
    };

    return (
        <div className="space-y-3">
            {items.map((opt, i) => (
                <div key={i} className="border border-gray-200 rounded-lg p-3 space-y-2">
                    <div className="flex gap-2 items-center">
                        <input
                            value={opt.optionName}
                            onChange={e => updateOption(i, "optionName", e.target.value)}
                            className="flex-1 border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white"
                            placeholder="옵션명 (예: 기본형, 1억 플랜)"
                        />
                        <label className="flex items-center gap-1.5 text-xs text-gray-600 cursor-pointer whitespace-nowrap">
                            <input type="checkbox" checked={opt.isDefault} onChange={e => updateOption(i, "isDefault", e.target.checked)} className="accent-blue-600" />
                            기본값
                        </label>
                        <button type="button" onClick={() => removeOption(i)} className="text-red-400 hover:text-red-600 text-sm">✕</button>
                    </div>
                    <div className="pl-2 space-y-1">
                        {opt.details.map((det, j) => (
                            <div key={j} className="flex gap-2 items-center">
                                <select
                                    value={det.detailType}
                                    onChange={e => updateDetail(i, j, "detailType", e.target.value)}
                                    className="w-28 border border-gray-200 rounded-md px-2.5 py-[7px] text-xs outline-none focus:border-blue-500 bg-white"
                                >
                                    {LIMIT_DETAIL_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                                </select>
                                <input
                                    type="number"
                                    value={det.amount}
                                    onChange={e => updateDetail(i, j, "amount", e.target.value)}
                                    className="w-40 border border-gray-200 rounded-md px-2.5 py-[7px] text-xs outline-none focus:border-blue-500 bg-white"
                                    placeholder="금액 (빈칸=무한)"
                                />
                                <button type="button" onClick={() => removeDetail(i, j)} className="text-red-300 hover:text-red-500 text-xs">✕</button>
                            </div>
                        ))}
                        <button type="button" onClick={() => addDetail(i)} className="text-xs text-gray-400 hover:text-gray-600">+ 항목 추가</button>
                    </div>
                </div>
            ))}
            <button type="button" onClick={addOption} className="text-xs text-blue-500 hover:text-blue-700">+ 옵션 추가</button>
        </div>
    );
}
