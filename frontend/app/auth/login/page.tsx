'use client';

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { login, saveSession } from "@/queries/auth";
import { ApiError } from "@/queries/api";
import { useAuthStore } from "@/store/authStore";

export default function LoginPage() {
    const [userId, setUserId] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();
    const { login: storeLogin } = useAuthStore();

    const saveSessionAndRedirect = (res: any) => {
        saveSession(res);
        storeLogin(res.role, res.name, res.userId);
        if (res.role === "ADMIN" || res.role === "EMPLOYEE") {
            router.push("/employee/dashboard");
        } else {
            router.push("/");
        }
        router.refresh();
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const res = await login({ userId, password });
            saveSessionAndRedirect(res);
        } catch (error) {
            if (error instanceof ApiError) {
                alert(`로그인 실패: ${error.message}`);
            } else {
                alert("알 수 없는 오류가 발생했습니다.");
            }
        }
    };

    return (
        <main style={{ maxWidth: 420, width: "100%", margin: "80px auto", padding: "0 20px" }}>
            <h1 className="text-2xl font-bold mb-6 text-center">로그인</h1>
            <form onSubmit={handleLogin} className="flex flex-col gap-4">
                <div>
                    <label className="block text-sm font-medium mb-1">아이디</label>
                    <input
                        type="text"
                        required
                        value={userId}
                        onChange={(e) => setUserId(e.target.value)}
                        className="w-full border p-2 rounded"
                        placeholder="아이디 입력"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-1">비밀번호</label>
                    <input
                        type="password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full border p-2 rounded"
                        placeholder="비밀번호 입력"
                    />
                </div>
                <button
                    type="submit"
                    className="w-full bg-blue-500 text-white font-bold p-3 rounded mt-2 hover:bg-blue-600 transition"
                >
                    로그인
                </button>
            </form>

            <div className="my-6 border-t border-gray-200" />

            <Link
                href="/auth/identity-login"
                className="w-full inline-flex items-center justify-center bg-gray-100 text-gray-800 font-semibold p-3 rounded hover:bg-gray-200 transition"
            >
                본인인증으로 로그인
            </Link>

            <div className="text-center mt-4 text-sm text-gray-500">
                계정이 없으신가요?
                <Link href="/auth/signup" className="text-blue-500 underline ml-2">회원가입</Link>
            </div>
        </main>
    );
}
