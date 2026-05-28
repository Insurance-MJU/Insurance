import Link from 'next/link';
import type { ReactNode } from 'react';

export default function CustomerLayout({ children }: { children: ReactNode }) {
    return (
        <>
            <header className="sticky top-0 z-50 bg-white border-b border-gray-100 shadow-sm">
                <div className="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
                    <Link href="/" className="font-extrabold text-slate-900 text-lg">한국생명보험</Link>
                    <nav className="flex items-center gap-6 text-sm font-medium text-slate-600">
                        <Link href="/insurance/products" className="hover:text-blue-600 transition">상품</Link>
                        <Link href="/insurance/contracts" className="hover:text-blue-600 transition">내 계약</Link>
                        <Link href="/insurance/claims" className="hover:text-blue-600 transition">사고접수</Link>
                        <Link href="/auth/login" className="px-4 py-1.5 bg-slate-900 text-white rounded-lg hover:bg-slate-800 transition">로그인</Link>
                    </nav>
                </div>
            </header>
            <div className="flex-1">{children}</div>
        </>
    );
}
