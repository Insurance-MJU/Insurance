import React, { SelectHTMLAttributes } from 'react';

export interface SelectOption {
    value: string | number;
    label: string;
}

interface SelectFieldProps extends SelectHTMLAttributes<HTMLSelectElement> {
    label: string;
    options: readonly SelectOption[] | SelectOption[];
    error?: string;
}

export function SelectField({ label, options, error, className = "", ...props }: SelectFieldProps) {
    return (
        <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">
                {label}
            </label>
            <select
                className={`w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white ${className}`}
                {...props}
            >
                {options.map(o => (
                    <option key={o.value} value={o.value}>
                        {o.label}
                    </option>
                ))}
            </select>
            {error && <span className="text-xs text-red-500 mt-1 block">{error}</span>}
        </div>
    );
}
