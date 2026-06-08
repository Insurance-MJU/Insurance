'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createRider } from "@/queries/master";
import RiderFormFields from "@/components/master/RiderFormFields";

const emptyForm = () => ({
    riderCode: "", name: "", description: "",
    riderType: "DISCOUNT", discountRate: null as number | null,
    mandatory: false, provisionId: null as number | null,
    exclusions: [] as any[],
});

export default function RiderNewPage() {
    const router = useRouter();
    const [form, setForm] = useState(emptyForm());
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: any) => setForm(f => ({ ...f, [k]: v }));

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.riderCode.trim()) { setError("특약 코드를 입력하세요."); return; }
        if (!form.name.trim()) { setError("특약명을 입력하세요."); return; }
        setLoading(true); setError("");
        try {
            await createRider(form);
            router.push("/employee/master/rider");
        } catch (e: any) { setError(e.message ?? "등록 실패"); }
        finally { setLoading(false); }
    };

    return (
        <div className="max-w-3xl">
            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <h1 className="text-xl font-bold text-gray-800">특약 등록</h1>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6">
                <RiderFormFields form={form} set={set} />
                <div className="flex gap-3 mt-5">
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
