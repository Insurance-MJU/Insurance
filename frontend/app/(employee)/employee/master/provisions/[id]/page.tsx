'use client';

import { useEffect, useState, useCallback } from "react";
import { useParams, useRouter } from "next/navigation";
import { getProvision, getProvisionTree, addProvisionItem, updateProvisionItem, deleteProvisionItem } from "@/queries/master";
import { LEVEL_TYPES } from "@/types/master";

// ── 레벨별 색상 ────────────────────────────────────────────────────────────
const LEVEL_COLOR: Record<string, string> = {
    PART:      "bg-indigo-100 text-indigo-700",
    CHAPTER:   "bg-blue-100 text-blue-700",
    SECTION:   "bg-sky-100 text-sky-700",
    ARTICLE:   "bg-green-100 text-green-700",
    PARAGRAPH: "bg-amber-100 text-amber-700",
    ITEM:      "bg-orange-100 text-orange-700",
    SUB_ITEM:  "bg-gray-100 text-gray-600",
};

// ── 항목 추가/수정 모달 ────────────────────────────────────────────────────
const emptyForm = () => ({ levelType: "ARTICLE", label: "", title: "", content: "" });

function ItemModal({ open, initial, onClose, onSave }: {
    open: boolean;
    initial: any | null;
    onClose: () => void;
    onSave: (form: any) => Promise<void>;
}) {
    const [form, setForm] = useState(emptyForm());
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: string) => setForm(f => ({ ...f, [k]: v }));

    useEffect(() => {
        if (open) { setForm(initial ?? emptyForm()); setError(""); }
    }, [open, initial]);

    if (!open) return null;

    const handleSave = async () => {
        setLoading(true); setError("");
        try { await onSave(form); onClose(); }
        catch (e: any) { setError(e.message ?? "저장 실패"); }
        finally { setLoading(false); }
    };

    const L = "block text-xs font-medium text-gray-500 mb-1";
    const I = "w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white";

    return (
        <div className="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-xl w-full max-w-lg shadow-xl">
                <div className="px-6 py-4 border-b border-gray-100">
                    <h3 className="text-base font-bold text-gray-800">{initial ? "항목 수정" : "항목 추가"}</h3>
                </div>
                <div className="p-6 space-y-4">
                    <div className="grid grid-cols-2 gap-3">
                        <div>
                            <label className={L}>계층 유형</label>
                            <select value={form.levelType} onChange={e => set("levelType", e.target.value)} className={I}>
                                {LEVEL_TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className={L}>라벨 (제1조, ①, 1, 가 등)</label>
                            <input value={form.label} onChange={e => set("label", e.target.value)} className={I} placeholder="제3조" />
                        </div>
                    </div>
                    <div>
                        <label className={L}>제목</label>
                        <input value={form.title} onChange={e => set("title", e.target.value)} className={I} placeholder="보상하는 손해" />
                    </div>
                    <div>
                        <label className={L}>내용</label>
                        <textarea value={form.content} onChange={e => set("content", e.target.value)}
                            className={`${I} min-h-[100px] resize-y`} placeholder="조항 본문을 입력하세요" />
                    </div>
                    {error && <p className="text-sm text-red-500">{error}</p>}
                </div>
                <div className="px-6 py-4 border-t border-gray-100 flex justify-end gap-3">
                    <button type="button" onClick={onClose}
                        className="px-4 py-2 border border-gray-300 text-gray-600 text-sm rounded-lg hover:bg-gray-50">
                        취소
                    </button>
                    <button type="button" onClick={handleSave} disabled={loading}
                        className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "저장 중..." : "저장"}
                    </button>
                </div>
            </div>
        </div>
    );
}

// ── 트리 노드 ─────────────────────────────────────────────────────────────
function TreeNode({ item, depth, provisionId, onRefresh }: {
    item: any;
    depth: number;
    provisionId: number;
    onRefresh: () => void;
}) {
    const [expanded, setExpanded] = useState(depth < 2);
    const [addOpen, setAddOpen] = useState(false);
    const [editOpen, setEditOpen] = useState(false);
    const hasChildren = item.children?.length > 0;

    const handleDelete = async () => {
        if (!confirm(`"${item.label || item.title}" 항목(및 하위 항목)을 삭제하시겠습니까?`)) return;
        await deleteProvisionItem(item.id);
        onRefresh();
    };

    const handleAdd = async (form: any) => {
        await addProvisionItem(provisionId, { ...form, parentId: item.id });
        setExpanded(true);
        onRefresh();
    };

    const handleEdit = async (form: any) => {
        await updateProvisionItem(item.id, { ...form, parentId: item.parentId, sortOrder: item.sortOrder });
        onRefresh();
    };

    const indent = depth * 20;

    return (
        <>
            <div className="group">
                <div className="flex items-start gap-2 py-1.5 px-2 rounded-md hover:bg-gray-50"
                    style={{ paddingLeft: `${indent + 8}px` }}>
                    <button type="button"
                        onClick={() => setExpanded(e => !e)}
                        className="mt-0.5 shrink-0 w-4 text-center text-gray-400 text-xs">
                        {hasChildren ? (expanded ? "▾" : "▸") : "·"}
                    </button>
                    <span className={`shrink-0 px-1.5 py-0.5 rounded text-xs font-medium ${LEVEL_COLOR[item.levelType] ?? "bg-gray-100 text-gray-600"}`}>
                        {item.levelTypeDisplayName}
                    </span>
                    {item.label && <span className="text-sm font-semibold text-gray-700 shrink-0">{item.label}</span>}
                    {item.title && <span className="text-sm text-gray-700">{item.title}</span>}
                    <div className="ml-auto flex gap-2 opacity-0 group-hover:opacity-100 shrink-0">
                        <button type="button" onClick={() => setAddOpen(true)}
                            className="text-xs text-blue-500 hover:text-blue-700">+ 하위</button>
                        <button type="button" onClick={() => setEditOpen(true)}
                            className="text-xs text-gray-400 hover:text-gray-600">수정</button>
                        <button type="button" onClick={handleDelete}
                            className="text-xs text-red-400 hover:text-red-600">삭제</button>
                    </div>
                </div>
                {item.content && expanded && (
                    <div className="text-xs text-gray-500 whitespace-pre-wrap py-1 leading-relaxed"
                        style={{ paddingLeft: `${indent + 36}px`, paddingRight: "8px" }}>
                        {item.content}
                    </div>
                )}
            </div>
            {expanded && hasChildren && item.children.map((child: any) => (
                <TreeNode key={child.id} item={child} depth={depth + 1} provisionId={provisionId} onRefresh={onRefresh} />
            ))}

            <ItemModal open={addOpen} initial={null} onClose={() => setAddOpen(false)} onSave={handleAdd} />
            <ItemModal open={editOpen}
                initial={{ levelType: item.levelType, label: item.label ?? "", title: item.title ?? "", content: item.content ?? "" }}
                onClose={() => setEditOpen(false)} onSave={handleEdit} />
        </>
    );
}

// ── 메인 페이지 ───────────────────────────────────────────────────────────
export default function ProvisionDetailPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const provisionId = Number(id);

    const [doc, setDoc] = useState<any>(null);
    const [items, setItems] = useState<any[]>([]);
    const [addRootOpen, setAddRootOpen] = useState(false);
    const [error, setError] = useState("");

    const load = useCallback(async () => {
        try {
            const [d, tree] = await Promise.all([
                getProvision(provisionId),
                getProvisionTree(provisionId),
            ]);
            setDoc(d);
            setItems(tree);
        } catch {
            setError("약관을 불러올 수 없습니다.");
        }
    }, [provisionId]);

    useEffect(() => { load(); }, [load]);

    const handleAddRoot = async (form: any) => {
        await addProvisionItem(provisionId, { ...form, parentId: null });
        load();
    };

    if (!doc) return <p className="text-sm text-gray-500 p-6">{error || "불러오는 중..."}</p>;

    return (
        <div className="max-w-4xl">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button type="button" onClick={() => router.back()}
                        className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                    <h1 className="text-xl font-bold text-gray-800">{doc.title}</h1>
                </div>
                <button type="button" onClick={() => setAddRootOpen(true)}
                    className="px-4 py-1.5 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                    + 최상위 항목 추가
                </button>
            </div>

            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}

            <div className="bg-white border border-gray-200 rounded-xl p-4 min-h-[200px]">
                {items.length === 0 ? (
                    <div className="flex flex-col items-center justify-center py-16 text-gray-400">
                        <p className="text-sm">등록된 조항이 없습니다.</p>
                        <p className="text-xs mt-1">위 버튼으로 최상위 항목(편/장/조 등)을 추가하세요.</p>
                    </div>
                ) : (
                    items.map((item: any) => (
                        <TreeNode key={item.id} item={item} depth={0} provisionId={provisionId} onRefresh={load} />
                    ))
                )}
            </div>

            <ItemModal open={addRootOpen} initial={null}
                onClose={() => setAddRootOpen(false)} onSave={handleAddRoot} />
        </div>
    );
}
