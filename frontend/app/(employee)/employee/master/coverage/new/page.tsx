'use client';

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getCoverages, createCoverage } from "@/queries/master";
import CoverageFormFields from "@/components/master/CoverageFormFields";

const emptyForm = () => ({
    coverageType: "BODILY_INJURY_1", name: "", description: "",
    mandatory: false, limitType: "UNLIMITED", limitAmount: "",
    limitUnit: "PER_PERSON", compensationType: "ACTUAL_LOSS",
    autoRestoration: false, excessPay: false,
    deductibleType: "NONE", deductibleAmount: "", deductibleRate: "",
    provisionId: null as number | null,
    exclusions: [] as any[],
    requiredCoverages: [] as string[], limitOptions: [] as any[],
});

export default function CoverageNewPage() {
    const router = useRouter();
    const [form, setForm] = useState(emptyForm());
    const [existingTypes, setExistingTypes] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const set = (k: string, v: any) => setForm(f => ({ ...f, [k]: v }));

    useEffect(() => {
        getCoverages().then((list: any[]) => setExistingTypes(list.map(c => c.coverageType))).catch(() => {});
    }, []);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!form.name.trim()) { setError("담보명을 입력하세요."); return; }
        if (existingTypes.includes(form.coverageType)) { setError("이미 등록된 담보 종류입니다."); return; }
        setLoading(true); setError("");
        try {
            await createCoverage({
                ...form,
                limitAmount: form.limitType === "FIXED" && form.limitOptions.length === 0 ? Number(form.limitAmount) || null : null,
                deductibleAmount: form.deductibleType === "FIXED_AMOUNT" ? Number(form.deductibleAmount) : null,
                deductibleRate: form.deductibleType === "RATE" ? Number(form.deductibleRate) : null,
                limitOptions: form.limitOptions.map((o: any) => ({
                    optionName: o.optionName, isDefault: o.isDefault,
                    details: o.details.map((d: any) => ({ detailType: d.detailType, amount: d.amount !== "" ? Number(d.amount) : null })),
                })),
            });
            router.push("/employee/master/coverage");
        } catch (e: any) {
            const msg: string = e.message ?? "등록 실패";
            setError(msg.includes("Duplicate entry") ? "이미 등록된 담보 종류입니다." : msg);
        } finally { setLoading(false); }
    };

    return (
        <div className="max-w-3xl">
            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <h1 className="text-xl font-bold text-gray-800">담보 등록</h1>
            </div>
            {error && <p className="mb-4 text-sm text-red-500">{error}</p>}
            <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6">
                <CoverageFormFields form={form} set={set} />
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
