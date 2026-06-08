'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createExclusion } from "@/queries/master";
import { EXCLUSION_TYPES } from "@/types/master";

type SubItem = { label: string; content: string; isException: boolean };
const emptySubItem = (): SubItem => ({ label: "", content: "", isException: false });
const I = "border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white";

export default function ExclusionNewPage() {
    const router = useRouter();
    const [form, setForm] = useState({ name: "", exclusionType: "INTENTIONAL", description: "" });
    const [subItems, setSubItems] = useState<SubItem[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: any) => setForm(f => ({ ...f, [k]: v }));

    const addSub = () => setSubItems(s => [...s, emptySubItem()]);
    const removeSub = (i: number) => setSubItems(s => s.filter((_, idx) => idx !== i));
    const updateSub = (i: number, key: keyof SubItem, val: any) =>
        setSubItems(s => s.map((item, idx) => idx === i ? { ...item, [key]: val } : item));

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault(); setLoading(true); setError("");
        try {
            await createExclusion({ ...form, subItems });
            router.push("/employee/master/exclusion");
        } catch (e: any) { setError(e.message ?? "등록 실패"); }
        finally { setLoading(false); }
    };

    return (
        <div className="max-w-xl">
            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <h1 className="text-xl font-bold text-gray-800">면책사유 등록</h1>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6 space-y-4">
                <div className="grid grid-cols-2 gap-4">
                    <div><label className="block text-xs font-medium text-gray-500 mb-1">면책 유형</label>
                        <select value={form.exclusionType} onChange={e => set("exclusionType", e.target.value)} className={`w-full ${I}`}>
                            {EXCLUSION_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                        </select></div>
                    <div><label className="block text-xs font-medium text-gray-500 mb-1">면책 사유명</label>
                        <input value={form.name} onChange={e => set("name", e.target.value)} className={`w-full ${I}`} placeholder="예: 고의적 사고" required /></div>
                    <div className="col-span-2"><label className="block text-xs font-medium text-gray-500 mb-1">본문 설명</label>
                        <textarea value={form.description} onChange={e => set("description", e.target.value)} className={`w-full ${I} min-h-[72px] resize-y`} /></div>
                </div>

                <div>
                    <div className="flex items-center justify-between mb-2">
                        <label className="text-xs font-medium text-gray-500">가/나/다 서브항목</label>
                        <button type="button" onClick={addSub} className="text-xs text-blue-500 hover:text-blue-700">+ 항목 추가</button>
                    </div>
                    {subItems.length > 0 && (
                        <div className="space-y-2 pl-2 border-l-2 border-gray-200">
                            {subItems.map((s, i) => (
                                <div key={i} className="flex gap-2 items-center">
                                    <input value={s.label} onChange={e => updateSub(i, "label", e.target.value)}
                                        className={`w-10 ${I} text-xs`} placeholder="가" />
                                    <input value={s.content} onChange={e => updateSub(i, "content", e.target.value)}
                                        className={`flex-1 ${I} text-xs`} placeholder="상세 내용" />
                                    <label className="flex items-center gap-1 text-xs text-gray-500 whitespace-nowrap cursor-pointer">
                                        <input type="checkbox" checked={s.isException} onChange={e => updateSub(i, "isException", e.target.checked)} className="accent-green-500" />
                                        예외
                                    </label>
                                    <button type="button" onClick={() => removeSub(i)} className="text-red-300 hover:text-red-500 text-xs">✕</button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className="flex gap-3 pt-2">
                    <button type="submit" disabled={loading} className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "등록 중..." : "등록"}
                    </button>
                    <button type="button" onClick={() => router.back()} className="px-5 py-2 border border-gray-300 text-gray-600 text-sm rounded-lg hover:bg-gray-50">취소</button>
                </div>
            </form>
        </div>
    );
}
