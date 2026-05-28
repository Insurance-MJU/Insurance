'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { login } from '@/queries/auth';
import { saveSession, ApiError } from '@/queries/api';

export default function LoginPage() {
    const [userId, setUserId] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const router = useRouter();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await login({ userId, password });
            saveSession(res);
            if (res.role === 'EMPLOYEE' || res.role === 'ADMIN') {
                router.push('/employee/dashboard');
            } else {
                router.push('/');
            }
            router.refresh();
        } catch (error) {
            alert(error instanceof ApiError ? `로그인 실패: ${error.message}` : '알 수 없는 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="min-h-[100dvh] flex items-center justify-center bg-gradient-to-br from-indigo-50 via-white to-blue-50 px-4">
            <div className="w-full max-w-sm bg-white rounded-2xl shadow-xl p-8 border border-gray-100">
                <h1 className="text-2xl font-extrabold text-slate-900 text-center mb-2">한국생명보험</h1>
                <p className="text-sm text-slate-500 text-center mb-8">로그인하여 서비스를 이용하세요</p>

                <form onSubmit={handleLogin} className="flex flex-col gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">아이디</label>
                        <input
                            type="text"
                            required
                            value={userId}
                            onChange={e => setUserId(e.target.value)}
                            className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="아이디 입력"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">비밀번호</label>
                        <input
                            type="password"
                            required
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="비밀번호 입력"
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-slate-900 hover:bg-slate-800 disabled:bg-slate-400 text-white font-bold py-3 rounded-lg mt-2 transition"
                    >
                        {loading ? '로그인 중...' : '로그인'}
                    </button>
                </form>
            </div>
        </main>
    );
}
