'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createProvision } from "@/queries/master";

export default function ProvisionsNewPage() {
    const router = useRouter();
    const [title, setTitle] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!title.trim()) { setError("약관 제목을 입력하세요."); return; }
        setLoading(true); setError("");
        try {
            const doc = await createProvision({ title });
            router.push(`/employee/master/provisions/${doc.id}`);
        } catch (e: any) {
            setError(e.message ?? "등록 실패");
        } finally { setLoading(false); }
    };

    return (
        <div className="max-w-xl">
            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <h1 className="text-xl font-bold text-gray-800">표준약관 등록</h1>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6 space-y-4">
                <div>
                    <label className="block text-xs font-medium text-gray-500 mb-1">약관 제목</label>
                    <input
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        className="w-full border border-gray-200 rounded-md px-2.5 py-[7px] text-sm outline-none focus:border-blue-500 bg-white"
                        placeholder="예: 2026년 개인용 자동차보험 표준약관"
                        required
                    />
                    <p className="mt-1 text-xs text-gray-400">등록 후 상세 화면에서 편·장·절·조 구조를 입력할 수 있습니다.</p>
                </div>
                <div className="flex gap-3 pt-2">
                    <button type="submit" disabled={loading}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "등록 중..." : "등록"}
                    </button>
                    <button type="button" onClick={() => router.back()}
                        className="px-5 py-2 border border-gray-300 text-gray-600 text-sm rounded-lg hover:bg-gray-50">
                        취소
                    </button>
                </div>
            </form>
        </div>
    );
}
