'use client';

import { useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { useProductEditData, useUpdateProduct } from "@/queries/products";
import { useProductEditForm } from "@/hooks/useProductEditForm";
import { StepIndicator } from "@/components/common/ui/StepIndicator";
import { PRODUCT_WIZARD_STEPS } from "@/constants/product";
import { SelCoverages } from "@/types/product";
import { Step0BasicInfo } from "../../_components/steps/Step0BasicInfo";
import { Step1Coverages } from "../../_components/steps/Step1Coverages";
import { Step2Riders } from "../../_components/steps/Step2Riders";

const STEPS = [...PRODUCT_WIZARD_STEPS];

export default function ProductEditPage() {
    const { id } = useParams();
    const router = useRouter();
    const pid = id as any;

    const { data, isLoading: initialLoading, isError } = useProductEditData(pid);
    const { mutateAsync: updateProduct, isPending: loading } = useUpdateProduct();

    const form = useProductEditForm();

    // Pre-fill data when loaded
    useEffect(() => {
        if (!data) return;
        const { product: p } = data;
        form.setInfo({
            productCode:    p.productCode ?? "",
            productName:    p.productName ?? "",
            lineOfBusiness: p.lineOfBusiness ?? "PERSONAL_AUTO",
            targetCustomer: p.targetCustomer ?? "",
            saleStartDate:  p.saleStartDate ?? "",
            saleEndDate:    p.saleEndDate ?? "",
            status:         p.status ?? "DESIGNING",
            description:    p.description ?? "",
        });

        const covMap: SelCoverages = {};
        (p.coverages ?? []).forEach((c: any) => {
            const optIds = new Set<number>(
                (c.limitOptions ?? []).map((o: any) => o.id as number)
            );
            covMap[c.coverageMasterId] = optIds;
        });
        form.setSelCoverages(covMap);

        form.setSelRiders(new Set((p.riders ?? []).map((r: any) => r.riderId as number)));
    }, [data, form.setInfo, form.setSelCoverages, form.setSelRiders]);

    const handleSubmit = async () => {
        form.setError("");
        try {
            await updateProduct({
                id: pid,
                data: {
                    ...form.info,
                    saleEndDate: form.info.saleEndDate || null,
                    coverages: Object.entries(form.selCoverages).map(([cid, opts], idx) => ({
                        coverageMasterId: Number(cid),
                        limitOptionIds: Array.from(opts),
                        sortOrder: idx,
                    })),
                    riders: Array.from(form.selRiders).map((rid, idx) => ({
                        riderId: rid, isDefault: false, sortOrder: idx,
                    })),
                }
            });
            router.push(`/employee/products/${pid}`);
        } catch (e: any) {
            form.setError(e.message ?? "수정 실패");
        }
    };

    if (initialLoading) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;
    if (isError) return <p className="text-red-500 text-sm p-6">상품 정보 로드 실패</p>;

    return (
        <div className="max-w-3xl">
            <StepIndicator steps={STEPS} currentStep={form.step} />

            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 뒤로</button>
                <h1 className="text-xl font-bold text-gray-800">상품 수정</h1>
                <span className="text-sm text-gray-400 font-mono">{form.info.productCode}</span>
            </div>
            {form.error && <p className="mb-4 text-sm text-red-500">{form.error}</p>}

            <div className="bg-white border border-gray-200 rounded-xl p-6">
                {form.step === 0 && <Step0BasicInfo info={form.info} setInfo={form.setInfo} />}
                {form.step === 1 && (
                    <Step1Coverages
                        allCoverages={data?.coverages ?? []}
                        selCoverages={form.selCoverages}
                        toggleCoverage={form.toggleCoverage}
                        toggleOption={form.toggleOption}
                    />
                )}
                {form.step === 2 && (
                    <Step2Riders
                        allRiders={data?.riders ?? []}
                        selRiders={form.selRiders}
                        toggleRider={form.toggleRider}
                    />
                )}
            </div>

            <div className="flex justify-between mt-4">
                <button type="button"
                    onClick={() => form.step > 0 ? form.handlePrev() : router.back()}
                    className="px-5 py-2 border border-gray-300 text-gray-600 text-sm rounded-lg hover:bg-gray-50">
                    {form.step === 0 ? "취소" : "이전"}
                </button>
                {form.step < STEPS.length - 1 ? (
                    <button type="button" onClick={form.handleNext}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                        다음
                    </button>
                ) : (
                    <button type="button" onClick={handleSubmit} disabled={loading}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "저장 중..." : "수정 완료"}
                    </button>
                )}
            </div>
        </div>
    );
}
