'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getCoverage, updateCoverage, deleteCoverage } from "@/queries/master";
import CoverageFormFields from "@/components/master/CoverageFormFields";

function toForm(c: any) {
    return {
        coverageType: c.coverageType ?? "BODILY_INJURY_1",
        name: c.name ?? "", description: c.description ?? "",
        mandatory: c.mandatory ?? false, limitType: c.limitType ?? "UNLIMITED",
        limitAmount: c.limitAmount ?? "", limitUnit: c.limitUnit ?? "PER_PERSON",
        compensationType: c.compensationType ?? "ACTUAL_LOSS",
        autoRestoration: c.autoRestoration ?? false, excessPay: c.excessPay ?? false,
        deductibleType: c.deductibleType ?? "NONE",
        deductibleAmount: c.deductibleAmount ?? "", deductibleRate: c.deductibleRate ?? "",
        provisionId: c.provisionId ?? null,
        exclusions: (c.exclusions ?? []).map((e: any) => ({
            name: e.name, description: e.description,
            subItems: (e.subItems ?? []).map((s: any) => ({ label: s.label ?? "", content: s.content, isException: s.isException })),
        })),
        requiredCoverages: c.requiredCoverages ?? [],
        limitOptions: (c.limitOptions ?? []).map((o: any) => ({
            optionName: o.optionName, isDefault: o.isDefault,
            details: (o.details ?? []).map((d: any) => ({ detailType: d.detailType, amount: d.amount ?? "" })),
        })),
    };
}

function serializeForm(form: any) {
    return {
        ...form,
        limitAmount: form.limitType === "FIXED" && form.limitOptions.length === 0 ? Number(form.limitAmount) || null : null,
        deductibleAmount: form.deductibleType === "FIXED_AMOUNT" ? Number(form.deductibleAmount) : null,
        deductibleRate: form.deductibleType === "RATE" ? Number(form.deductibleRate) : null,
        limitOptions: form.limitOptions.map((o: any) => ({
            optionName: o.optionName, isDefault: o.isDefault,
            details: o.details.map((d: any) => ({ detailType: d.detailType, amount: d.amount !== "" ? Number(d.amount) : null })),
        })),
    };
}

export default function CoverageEditPage() {
    const { id } = useParams<{ id: string }>();
    const router = useRouter();
    const [form, setForm] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: any) => setForm((f: any) => ({ ...f, [k]: v }));

    useEffect(() => {
        getCoverage(id).then(c => setForm(toForm(c))).catch(() => setError("담보를 불러올 수 없습니다."));
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.name.trim()) { setError("담보명을 입력하세요."); return; }
        setLoading(true); setError("");
        try { await updateCoverage(id, serializeForm(form)); router.push(`/employee/master/coverage/${id}`); }
        catch (e: any) { setError(e.message ?? "저장 실패"); }
        finally { setLoading(false); }
    };

    const handleDelete = async () => {
        if (!confirm("담보를 삭제하시겠습니까?")) return;
        await deleteCoverage(id);
        router.push("/employee/master/coverage");
    };

    if (!form) return <p className="text-sm text-gray-500 p-6">{error || "불러오는 중..."}</p>;

    return (
        <div className="max-w-3xl">
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 뒤로</button>
                    <h1 className="text-xl font-bold text-gray-800">담보 수정</h1>
                    <span className="text-xs text-gray-400">ID: {id}</span>
                </div>
                <button type="button" onClick={handleDelete} className="text-xs text-red-400 hover:text-red-600">담보 삭제</button>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6">
                <CoverageFormFields form={form} set={set} />
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
