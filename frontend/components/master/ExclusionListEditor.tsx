'use client';

export type SubItem = { label: string; content: string; isException: boolean };
export type ExclusionItem = { name: string; description: string; subItems: SubItem[] };

interface Props {
    items: ExclusionItem[];
    onChange: (items: ExclusionItem[]) => void;
}

const emptySubItem = (): SubItem => ({ label: "", content: "", isException: false });
const emptyItem = (): ExclusionItem => ({ name: "", description: "", subItems: [] });

export default function ExclusionListEditor({ items, onChange }: Props) {
    const addItem = () => onChange([...items, emptyItem()]);
    const removeItem = (i: number) => onChange(items.filter((_, idx) => idx !== i));
    const updateItem = (i: number, key: keyof ExclusionItem, val: any) => {
        const next = [...items];
        next[i] = { ...next[i], [key]: val };
        onChange(next);
    };

    const addSub = (i: number) => updateItem(i, "subItems", [...items[i].subItems, emptySubItem()]);
    const removeSub = (i: number, j: number) =>
        updateItem(i, "subItems", items[i].subItems.filter((_, idx) => idx !== j));
    const updateSub = (i: number, j: number, key: keyof SubItem, val: any) => {
        const subs = [...items[i].subItems];
        subs[j] = { ...subs[j], [key]: val };
        updateItem(i, "subItems", subs);
    };

    return (
        <div className="space-y-3">
            {items.map((item, i) => (
                <div key={i} className="border border-gray-200 rounded-lg p-3 space-y-2 bg-gray-50">
                    <div className="flex gap-2 items-start">
                        <input
                            value={item.name}
                            onChange={e => updateItem(i, "name", e.target.value)}
                            className="w-36 shrink-0 border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white"
                            placeholder="면책사유명"
                        />
                        <input
                            value={item.description}
                            onChange={e => updateItem(i, "description", e.target.value)}
                            className="flex-1 border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white"
                            placeholder="본문 내용"
                        />
                        <button type="button" onClick={() => removeItem(i)} className="text-red-400 hover:text-red-600 text-sm shrink-0 mt-1.5">✕</button>
                    </div>
                    {item.subItems.length > 0 && (
                        <div className="pl-3 space-y-1 border-l-2 border-gray-200">
                            {item.subItems.map((sub, j) => (
                                <div key={j} className="flex gap-2 items-center">
                                    <input
                                        value={sub.label}
                                        onChange={e => updateSub(i, j, "label", e.target.value)}
                                        className="w-12 border border-gray-200 rounded-md px-2.5 py-[7px] text-xs outline-none focus:border-blue-500 bg-white"
                                        placeholder="가"
                                    />
                                    <input
                                        value={sub.content}
                                        onChange={e => updateSub(i, j, "content", e.target.value)}
                                        className="flex-1 border border-gray-200 rounded-md px-2.5 py-[7px] text-xs outline-none focus:border-blue-500 bg-white"
                                        placeholder="상세 내용"
                                    />
                                    <label className="flex items-center gap-1 text-xs text-gray-500 whitespace-nowrap cursor-pointer">
                                        <input type="checkbox" checked={sub.isException} onChange={e => updateSub(i, j, "isException", e.target.checked)} className="accent-green-500" />
                                        예외
                                    </label>
                                    <button type="button" onClick={() => removeSub(i, j)} className="text-red-300 hover:text-red-500 text-xs">✕</button>
                                </div>
                            ))}
                        </div>
                    )}
                    <button type="button" onClick={() => addSub(i)} className="text-xs text-gray-400 hover:text-gray-600 pl-1">+ 가/나/다 항목 추가</button>
                </div>
            ))}
            <button type="button" onClick={addItem} className="text-xs text-blue-500 hover:text-blue-700">+ 면책사유 추가</button>
        </div>
    );
}
