'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useMasterData, useCreateProduct } from "@/queries/products";
import { useProductEditForm } from "@/hooks/useProductEditForm";
import { StepIndicator } from "@/components/common/ui/StepIndicator";
import { PRODUCT_WIZARD_STEPS } from "@/constants/product";
import { Step0BasicInfo } from "../_components/steps/Step0BasicInfo";
import { Step1Coverages } from "../_components/steps/Step1Coverages";
import { Step2Riders } from "../_components/steps/Step2Riders";

const STEPS = [...PRODUCT_WIZARD_STEPS];

const STANDARD_BASE_PREMIUM = 1_150_000;
const LEGAL_RESERVE_RATIO = 0.40;

export default function ProductNewPage() {
    const router = useRouter();
    const { data: masterData, isLoading: masterLoading } = useMasterData();
    const { mutateAsync: createProduct, isPending: loading } = useCreateProduct();
    const form = useProductEditForm();

    const [calcInput, setCalcInput] = useState({ targetSales: 5000, lossRatio: 72, salesExpense: 15, adminExpense: 10 });
    const [calcResult, setCalcResult] = useState<any>(null);

    const handleCalc = () => {
        const { targetSales, lossRatio, salesExpense, adminExpense } = calcInput;
        const finalPremium = STANDARD_BASE_PREMIUM;
        const reserve       = Math.round(finalPremium * LEGAL_RESERVE_RATIO);
        const netPremium    = Math.round(finalPremium * lossRatio / 100);
        const expensePremium = finalPremium - netPremium;
        const totalRevenue  = finalPremium * targetSales;
        const totalClaims   = Math.round(totalRevenue * lossRatio / 100);
        const totalExpenses = Math.round(totalRevenue * (salesExpense + adminExpense) / 100);
        const profit        = totalRevenue - totalClaims - totalExpenses;
        setCalcResult({ finalPremium, reserve, netPremium, expensePremium, totalRevenue, totalClaims, totalExpenses, profit });
    };

    const TARGET_MAP: Record<string, string> = {
        PERSONAL_AUTO:   'PERSONAL',
        COMMERCIAL_AUTO: 'BUSINESS',
        BUSINESS_AUTO:   'COMMERCIAL',
    };

    const handleSubmit = async () => {
        form.setError("");
        try {
            const riderCodes = (masterData?.riders ?? [])
                .filter((r: any) => form.selRiders.has(r.id ?? r.riderCode))
                .map((r: any) => r.riderCode);

            await createProduct({
                productCode:   form.info.productCode,
                productName:   form.info.productName,
                description:   form.info.description,
                target:        TARGET_MAP[form.info.lineOfBusiness] ?? 'PERSONAL',
                saleStartDate: form.info.saleStartDate,
                saleEndDate:   form.info.saleEndDate || null,
                riderCodes,
            });
            alert("상품 설계가 완료되었습니다.");
            router.push("/employee/products");
        } catch (e: any) {
            form.setError(e.message ?? "등록 실패");
        }
    };

    return (
        <div className="max-w-3xl">
            <StepIndicator steps={STEPS} currentStep={form.step} />

            <div className="flex items-center gap-3 mb-6">
                <button type="button" onClick={() => router.back()} className="text-sm text-gray-400 hover:text-gray-600">← 목록</button>
                <h1 className="text-xl font-bold text-gray-800">보험상품 신규 등록</h1>
            </div>
            {form.error && <p className="mb-4 text-sm text-red-500">{form.error}</p>}

            <div className="bg-white border border-gray-200 rounded-xl p-6">
                {form.step === 0 && <Step0BasicInfo info={form.info} setInfo={form.setInfo} />}
                {form.step === 1 && (
                    <Step1Coverages
                        allCoverages={masterData?.coverages ?? []}
                        selCoverages={form.selCoverages}
                        toggleCoverage={form.toggleCoverage}
                        toggleOption={form.toggleOption}
                    />
                )}
                {form.step === 2 && (
                    <Step2Riders
                        allRiders={masterData?.riders ?? []}
                        selRiders={form.selRiders}
                        toggleRider={form.toggleRider}
                    />
                )}
                {form.step === 3 && (
                    <div className="space-y-5">
                        <div>
                            <h2 className="text-sm font-semibold text-gray-700">보험료 산출 (CT-02)</h2>
                            <p className="text-xs text-gray-400 mt-0.5">수익성 분석 후 최종 기본 보험료를 확정합니다.</p>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            {[
                                { label: "목표 판매 건수", key: "targetSales", unit: "건" },
                                { label: "예상 손해율", key: "lossRatio", unit: "%" },
                                { label: "영업비율", key: "salesExpense", unit: "%" },
                                { label: "관리비율", key: "adminExpense", unit: "%" },
                            ].map(({ label, key, unit }) => (
                                <div key={key}>
                                    <label className="block text-xs text-gray-500 mb-1">{label} ({unit})</label>
                                    <input type="number"
                                        value={calcInput[key as keyof typeof calcInput]}
                                        onChange={e => setCalcInput(p => ({ ...p, [key]: Number(e.target.value) }))}
                                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                                </div>
                            ))}
                        </div>

                        <button type="button" onClick={handleCalc}
                            className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700">
                            보험료 산출
                        </button>

                        {calcResult && (
                            <div className="space-y-3">
                                <div className="bg-blue-50 border border-blue-100 rounded-xl p-4">
                                    <p className="text-xs text-blue-500 font-medium mb-3">담보별 산출 내역</p>
                                    <div className="grid grid-cols-2 gap-y-2 text-sm">
                                        {[
                                            ["순보험료 (위험보험료)", calcResult.netPremium],
                                            ["부가보험료 (사업비)", calcResult.expensePremium],
                                            ["최종 기본 보험료", calcResult.finalPremium],
                                            ["법정 준비금 적립액", calcResult.reserve],
                                        ].map(([label, val]) => (
                                            <div key={label as string} className="flex justify-between gap-2">
                                                <span className="text-gray-500 shrink-0">{label}</span>
                                                <span className="font-medium text-gray-800">{(val as number).toLocaleString()}원</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="bg-gray-50 border border-gray-200 rounded-xl p-4">
                                    <p className="text-xs text-gray-500 font-medium mb-3">수익성 시뮬레이션</p>
                                    <div className="grid grid-cols-2 gap-y-2 text-sm">
                                        {[
                                            ["총 수입보험료", calcResult.totalRevenue],
                                            ["예상 지급보험금", calcResult.totalClaims],
                                            ["예상 사업비", calcResult.totalExpenses],
                                            ["예상 이익", calcResult.profit],
                                        ].map(([label, val]) => (
                                            <div key={label as string} className="flex justify-between gap-2">
                                                <span className="text-gray-500 shrink-0">{label}</span>
                                                <span className={`font-medium ${label === "예상 이익" ? (calcResult.profit >= 0 ? "text-green-700" : "text-red-600") : "text-gray-800"}`}>
                                                    {(val as number).toLocaleString()}원
                                                </span>
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="bg-green-50 border border-green-200 rounded-xl p-3 text-sm text-green-700">
                                    ✓ 최종 확정 보험료: <strong>{calcResult.finalPremium.toLocaleString()}원</strong>
                                    &nbsp;·&nbsp;법정 준비금 적립 필요액: <strong>{calcResult.reserve.toLocaleString()}원</strong>
                                </div>
                            </div>
                        )}
                    </div>
                )}
                {masterLoading && form.step > 0 && form.step < 3 && (
                    <p className="text-sm text-gray-400 text-center py-4">마스터 데이터 로딩 중...</p>
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
                    <button type="button" onClick={handleSubmit} disabled={loading || !calcResult}
                        className="px-5 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-50">
                        {loading ? "등록 중..." : "상품 확정"}
                    </button>
                )}
            </div>
        </div>
    );
}
