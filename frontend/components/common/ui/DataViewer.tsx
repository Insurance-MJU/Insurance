import React from 'react';

export function DataSection({ title, children, headerClassName = '' }: { title: string; children: React.ReactNode; headerClassName?: string }) {
  return (
    <div className="border rounded-xl overflow-hidden bg-white">
      <div className={`bg-gray-50 px-4 py-2 text-xs font-semibold text-gray-500 ${headerClassName}`}>
        {title}
      </div>
      <div className="divide-y border-t border-gray-100">
        {children}
      </div>
    </div>
  );
}

export function DataRow({ label, value, valueClassName = '' }: { label: string; value: React.ReactNode; valueClassName?: string }) {
  return (
    <div className="flex justify-between items-center px-4 py-3">
      <span className="text-sm text-gray-500">{label}</span>
      <span className={`text-sm font-medium ${valueClassName}`}>{value}</span>
    </div>
  );
}
