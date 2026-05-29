'use client';

import Link from "next/link";
import IdentityLoginForm from "@/components/auth/IdentityLoginForm";

export default function IdentityLoginPage() {
    return (
        <main style={{ maxWidth: 420, width: "100%", margin: "80px auto", padding: "0 20px" }}>
            <h1 className="text-2xl font-bold mb-2 text-center">본인인증 로그인</h1>
            <p className="text-sm text-gray-500 text-center mb-6">회원가입 없이 본인인증으로 로그인합니다.</p>
            <IdentityLoginForm />
            <div className="text-center mt-4 text-sm">
                <Link href="/auth/login" className="text-blue-500 underline">이메일 로그인으로 돌아가기</Link>
            </div>
        </main>
    );
}
