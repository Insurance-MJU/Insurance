'use client';

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { signup } from "@/queries/auth";
import { ApiError } from "@/queries/api";

export default function SignupPage() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const router = useRouter();

    const handleSignup = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await signup({ name, email, password });
            alert("회원가입이 완료되었습니다.");
            router.push("/auth/login");
        } catch (error) {
            if (error instanceof ApiError) {
                alert(`회원가입 실패: ${error.message}`);
            } else {
                alert("알 수 없는 오류가 발생했습니다.");
            }
        }
    };

    return (
        <main style={{ maxWidth: 400, width: "100%", margin: "80px auto", padding: "0 20px" }}>
            <h1 className="text-2xl font-bold mb-6 text-center">회원가입</h1>
            <form onSubmit={handleSignup} className="flex flex-col gap-4">
                <div>
                    <label className="block text-sm font-medium mb-1">이름</label>
                    <input 
                        type="text" 
                        required 
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full border p-2 rounded"
                        placeholder="이름 입력"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium mb-1">이메일</label>
                    <input 
                        type="email" 
                        required 
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full border p-2 rounded"
                        placeholder="example@email.com"
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
                    가입하기
                </button>
            </form>
            <div className="text-center mt-4 text-sm text-gray-500">
                이미 계정이 있으신가요? 
                <Link href="/auth/login" className="text-blue-500 underline ml-2">로그인</Link>
            </div>
        </main>
    );
}
