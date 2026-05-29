'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getRider, updateRider, deleteRider } from "@/queries/master";
import RiderFormFields from "@/components/master/RiderFormFields";

function toForm(r: any) {
    return {
        riderCode: r.riderCode ?? "",
        name: r.name ?? "",
        description: r.description ?? "",
        riderType: r.riderType ?? "DISCOUNT",
        discountRate: r.discountRate ?? null,
        mandatory: r.mandatory ?? false,
        provisionId: r.provisionId ?? null,
        exclusions: (r.exclusions ?? []).map((e: any) => ({
            name: e.name,
            description: e.description ?? "",
            subItems: (e.subItems ?? []).map((s: any) => ({ label: s.label ?? "", content: s.content, isException: s.isException })),
        })),
    };
}

export default function RiderEditPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const [form, setForm] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: any) => setForm((f: any) => ({ ...f, [k]: v }));

    useEffect(() => {
        getRider(id).then(r => setForm(toForm(r))).catch(() => setError("특약을 불러올 수 없습니다."));
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.name.trim()) { setError("특약명을 입력하세요."); return; }
        setLoading(true); setError("");
        try {
            await updateRider(id, form);
            router.push(`/employee/master/rider/${id}`);
        } catch (e: any) { setError(e.message ?? "저장 실패"); }
        finally { setLoading(false); }
    };

    const handleDelete = async () => {
        if (!confirm("특약을 삭제하시겠습니까?")) return;
        await deleteRider(id);
        router.push("/employee/master/rider");
    };

    if (!form) return <p className="text-sm text-gray-500 p-6">{error || "불러오는 중..."}</p>;

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 뒤로</button>
                    <h1 className="text-xl font-bold text-gray-800">특약 수정</h1>
                    <span className="text-xs text-gray-400">ID: {id}</span>
                </div>
                <button type="button" onClick={handleDelete} className="text-xs text-red-400 hover:text-red-600">특약 삭제</button>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6">
                <RiderFormFields form={form} set={set} />
                <div className="flex gap-3 mt-5">
                    <button type="submit" disabled={loading}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "저장 중..." : "저장"}
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
