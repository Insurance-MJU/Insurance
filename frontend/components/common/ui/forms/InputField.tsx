import React, { InputHTMLAttributes } from 'react';

interface InputFieldProps extends InputHTMLAttributes<HTMLInputElement> {
    label: string;
    error?: string;
}

export function InputField({ label, error, className = "", ...props }: InputFieldProps) {
    return (
        <div>
            <label className="block text-xs font-medium text-gray-500 mb-1">
                {label}
            </label>
            <input
                className={`w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white ${className}`}
                {...props}
            />
            {error && <span className="text-xs text-red-500 mt-1 block">{error}</span>}
        </div>
    );
}
