import Link from 'next/link';
import type { ReactNode } from 'react';

const NAV = [
    { href: '/employee/dashboard', label: '대시보드' },
    { href: '/employee/products', label: '상품 관리' },
    { href: '/employee/subscriptions', label: '청약 심사' },
    { href: '/employee/accidents', label: '사고 접수' },
    { href: '/employee/claims', label: '보험금 처리' },
];

export default function EmployeeLayout({ children }: { children: ReactNode }) {
    return (
        <div className="min-h-[100dvh] flex">
            <aside className="w-56 shrink-0 bg-slate-900 text-slate-300 flex flex-col">
                <div className="px-6 py-5 border-b border-slate-700">
                    <p className="font-extrabold text-white text-base">한국생명보험</p>
                    <p className="text-xs text-slate-400 mt-0.5">직원 포털</p>
                </div>
                <nav className="flex flex-col gap-1 p-3 flex-1">
                    {NAV.map(n => (
                        <Link
                            key={n.href}
                            href={n.href}
                            className="px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-slate-700 hover:text-white transition"
                        >
                            {n.label}
                        </Link>
                    ))}
                </nav>
                <div className="p-4 border-t border-slate-700">
                    <Link href="/insurance/products" className="text-xs text-slate-400 hover:text-white transition">← 고객 화면으로</Link>
                </div>
            </aside>
            <main className="flex-1 bg-slate-50 overflow-auto">{children}</main>
        </div>
    );
}
