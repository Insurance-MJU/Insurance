'use client';

import { useEffect, useState } from "react";
import { getExclusions } from "@/queries/master";

const TYPE_COLORS: Record<string, string> = {
    INTENTIONAL:   "bg-red-100 text-red-700",
    UNLICENSED:    "bg-orange-100 text-orange-700",
    DRUNK_DRIVING: "bg-yellow-100 text-yellow-700",
    WAR:           "bg-purple-100 text-purple-700",
    NUCLEAR:       "bg-pink-100 text-pink-700",
    RACING:        "bg-blue-100 text-blue-700",
    OTHER:         "bg-gray-100 text-gray-600",
};

interface Props {
    onPick: (exclusions: any[]) => void;
    existingNames: string[];
}

export default function CommonExclusionPicker({ onPick, existingNames }: Props) {
    const [open, setOpen] = useState(false);
    const [list, setList] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);
    const [selected, setSelected] = useState<Set<number>>(new Set());

    useEffect(() => {
        if (open && list.length === 0) {
            setLoading(true);
            getExclusions().then(setList).finally(() => setLoading(false));
        }
        if (!open) setSelected(new Set());
    }, [open]);

    const toggle = (id: number) =>
        setSelected(prev => { const s = new Set(prev); s.has(id) ? s.delete(id) : s.add(id); return s; });

    const handleConfirm = () => {
        const picks = list.filter(e => selected.has(e.id));
        onPick(picks);
        setOpen(false);
    };

    const availableCount = list.filter(e => !existingNames.includes(e.name)).length;

    return (
        <>
            <button type="button" onClick={() => setOpen(true)}
                className="text-xs px-3 py-1.5 rounded-md border border-blue-200 text-blue-600 hover:bg-blue-50 transition-colors">
                공통 면책사유 불러오기
            </button>

            {open && (
                <div className="fixed inset-0 z-50 flex items-center justify-center">
                    <div className="absolute inset-0 bg-black/30" onClick={() => setOpen(false)} />

                    <div className="relative bg-white rounded-2xl shadow-xl w-[480px] max-h-[70vh] flex flex-col">
                        {/* header */}
                        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
                            <div>
                                <h3 className="text-sm font-semibold text-gray-800">공통 면책사유 선택</h3>
                                <p className="text-xs text-gray-400 mt-0.5">
                                    {selected.size > 0 ? `${selected.size}개 선택됨` : "항목을 선택하세요 (복수 선택 가능)"}
                                </p>
                            </div>
                            <button type="button" onClick={() => setOpen(false)}
                                className="text-gray-400 hover:text-gray-600 text-lg leading-none">✕</button>
                        </div>

                        {/* list */}
                        <div className="overflow-y-auto flex-1 p-3 space-y-2">
                            {loading && <p className="text-xs text-gray-400 text-center py-8">불러오는 중...</p>}
                            {!loading && availableCount === 0 && list.length > 0 && (
                                <p className="text-xs text-gray-400 text-center py-8">추가 가능한 면책사유가 없습니다</p>
                            )}
                            {!loading && list.length === 0 && (
                                <p className="text-xs text-gray-400 text-center py-8">등록된 공통 면책사유가 없습니다</p>
                            )}
                            {list.map((e: any) => {
                                const added = existingNames.includes(e.name);
                                const checked = selected.has(e.id);
                                return (
                                    <div key={e.id}
                                        onClick={() => { if (!added) toggle(e.id); }}
                                        className={`p-3 rounded-xl border transition-colors ${
                                            added
                                                ? "border-gray-100 bg-gray-50 opacity-50 cursor-not-allowed"
                                                : checked
                                                    ? "border-blue-400 bg-blue-50 cursor-pointer"
                                                    : "border-gray-200 hover:border-blue-300 hover:bg-gray-50 cursor-pointer"
                                        }`}>
                                        <div className="flex items-start justify-between gap-2 mb-1.5">
                                            <div className="flex items-start gap-2">
                                                {!added && (
                                                    <input type="checkbox" checked={checked} readOnly
                                                        className="mt-0.5 shrink-0 accent-blue-600" />
                                                )}
                                                <span className={`shrink-0 text-[10px] font-medium px-1.5 py-0.5 rounded-full ${TYPE_COLORS[e.exclusionType] ?? "bg-gray-100 text-gray-600"}`}>
                                                    {e.exclusionTypeDisplayName}
                                                </span>
                                                <span className={`text-sm font-medium ${added ? "text-gray-400" : checked ? "text-blue-700" : "text-gray-800"}`}>
                                                    {e.name}
                                                </span>
                                            </div>
                                            {added && <span className="shrink-0 text-[10px] text-gray-400 bg-gray-100 px-1.5 py-0.5 rounded-full">추가됨</span>}
                                        </div>
                                        {e.description && (
                                            <p className="text-xs text-gray-500 line-clamp-2 ml-5">{e.description}</p>
                                        )}
                                        {e.subItems?.length > 0 && (
                                            <div className="mt-2 ml-5 pl-2 border-l-2 border-gray-200 space-y-0.5">
                                                {e.subItems.slice(0, 3).map((s: any, i: number) => (
                                                    <p key={i} className="text-[11px] text-gray-400 truncate">
                                                        {s.label && <span className="font-medium text-gray-500">{s.label}. </span>}
                                                        {s.content}
                                                        {s.isException && <span className="ml-1 text-green-600">(예외)</span>}
                                                    </p>
                                                ))}
                                                {e.subItems.length > 3 && (
                                                    <p className="text-[11px] text-gray-400">외 {e.subItems.length - 3}개 항목</p>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>

                        {/* footer */}
                        <div className="px-4 py-3 border-t border-gray-100 flex justify-end gap-2">
                            <button type="button" onClick={() => setOpen(false)}
                                className="px-4 py-2 text-sm text-gray-500 hover:text-gray-700">
                                취소
                            </button>
                            <button type="button" onClick={handleConfirm} disabled={selected.size === 0}
                                className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed">
                                {selected.size > 0 ? `${selected.size}개 추가` : "추가"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
