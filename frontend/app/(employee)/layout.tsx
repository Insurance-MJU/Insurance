'use client';

import { useEffect } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { setAuthFailureHandler } from "@/queries/api";
import { clearSession } from "@/queries/auth";
import { useAuthStore } from "@/store/authStore";
import { EMPLOYEE_MENU } from "@/constants/employeeMenu";


export default function EmployeeLayout({ children }: { children: React.ReactNode }) {
    const pathname = usePathname();
    const router = useRouter();
    const { logout } = useAuthStore();

    useEffect(() => {
        setAuthFailureHandler(() => router.push("/auth/login"));
    }, [router]);

    return (
        <div className="flex min-h-screen bg-gray-50">
            {/* 사이드바 */}
            <aside className="w-60 bg-white border-r border-gray-200 flex flex-col">
                <div className="px-6 py-5 border-b border-gray-100">
                    <span className="text-lg font-bold text-blue-700">보험관리 시스템</span>
                    <p className="text-xs text-gray-400 mt-0.5">직원 포털</p>
                </div>
                <nav className="flex-1 py-4 overflow-y-auto">
                    {EMPLOYEE_MENU.map((item) => {
                        const parentActive = item.children.length > 0
                            ? pathname.startsWith(item.href)
                            : pathname === item.href;
                        return (
                            <div key={item.href}>
                                <Link
                                    href={item.href}
                                    className={`flex items-center px-6 py-2.5 text-sm font-medium transition-colors
                                        ${parentActive
                                            ? "bg-blue-50 text-blue-700 border-r-2 border-blue-700"
                                            : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"}`}
                                >
                                    {item.label}
                                </Link>
                                {item.children.length > 0 && (
                                    <div className="ml-4 border-l border-gray-100">
                                        {item.children.map((child) => {
                                            const childActive = (child as any).exact
                                                ? pathname === child.href
                                                : pathname === child.href || pathname.startsWith(child.href + "/");
                                            return (
                                                <Link
                                                    key={child.href}
                                                    href={child.href}
                                                    className={`flex items-center pl-4 pr-4 py-2 text-xs transition-colors
                                                        ${childActive
                                                            ? "text-blue-600 font-semibold"
                                                            : "text-gray-500 hover:text-gray-800"}`}
                                                >
                                                    {child.label}
                                                </Link>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </nav>
                <div className="px-6 py-4 border-t border-gray-100">
                    <button
                        onClick={() => {
                            clearSession();
                            logout();
                            window.location.href = "/auth/login";
                        }}
                        className="text-xs text-gray-400 hover:text-red-500 transition-colors"
                    >
                        로그아웃
                    </button>
                </div>
            </aside>

            {/* 메인 콘텐츠 */}
            <main className="flex-1 p-8 overflow-auto">
                {children}
            </main>
        </div>
    );
}
