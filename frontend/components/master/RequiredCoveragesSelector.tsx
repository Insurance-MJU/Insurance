'use client';

import { COVERAGE_TYPES } from "@/types/master";

interface Props {
    selected: string[];
    currentType: string;
    onChange: (selected: string[]) => void;
}

export default function RequiredCoveragesSelector({ selected, currentType, onChange }: Props) {
    const toggle = (value: string) => {
        onChange(selected.includes(value)
            ? selected.filter(v => v !== value)
            : [...selected, value]);
    };

    return (
        <div className="flex flex-wrap gap-2">
            {COVERAGE_TYPES.filter(t => t.value !== currentType).map(t => (
                <label key={t.value} className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg border text-xs cursor-pointer transition
                    ${selected.includes(t.value) ? "bg-blue-50 border-blue-400 text-blue-700" : "border-gray-200 text-gray-600 hover:border-gray-300"}`}>
                    <input type="checkbox" className="hidden" checked={selected.includes(t.value)} onChange={() => toggle(t.value)} />
                    {t.label}
                </label>
            ))}
        </div>
    );
}
