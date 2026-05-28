'use client';
import { useState } from 'react';
import { useQuery, useMutation } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { getCoverages, getRiders, calculatePremium } from '@/queries/products';
import { fetchApi } from '@/queries/api';
import type { Coverage, Rider } from '@/types';

const STEPS = ['상품 기본정보', '담보 선택', '특약 선택', '보험료 산출', '기초서류 등록', '설계 완료'];

export default function NewProductPage() {
    const router = useRouter();
    const [step, setStep] = useState(0);

    // Step 0 - 기본정보
    const [productCode, setProductCode] = useState('');
    const [productName, setProductName] = useState('');
    const [target, setTarget] = useState('PERSONAL');
    const [saleStartDate, setSaleStartDate] = useState('');
    const [saleEndDate, setSaleEndDate] = useState('');
    const [description, setDescription] = useState('');

    // Step 1 - 담보
    const [selectedCoverages, setSelectedCoverages] = useState<string[]>([]);
    const [selectedOptions, setSelectedOptions] = useState<Record<string, string[]>>({});

    // Step 2 - 특약
    const [selectedRiders, setSelectedRiders] = useState<string[]>([]);

    // Step 3 - 보험료 산출
    const [targetSales, setTargetSales] = useState(5000);
    const [lossRatio, setLossRatio] = useState(72.0);
    const [salesExpense, setSalesExpense] = useState(15.0);
    const [adminExpense, setAdminExpense] = useState(10.0);
    const [calcResult, setCalcResult] = useState<any>(null);

    // Step 4 - 기초서류
    const [documents, setDocuments] = useState(['사업방법서', '보험약관', '산출방법서']);

    // 저장된 productId
    const [savedProductId, setSavedProductId] = useState('');

    const { data: coverages } = useQuery<Coverage[]>({ queryKey: ['coverages'], queryFn: getCoverages });
    const { data: riders } = useQuery<Rider[]>({ queryKey: ['riders'], queryFn: getRiders });

    const createProduct = useMutation({
        mutationFn: () => fetchApi('/products', {
            method: 'POST',
            body: JSON.stringify({ productCode, productName, description, target, saleStartDate, saleEndDate, riderCodes: selectedRiders, coverageIds: selectedCoverages }),
        }),
        onSuccess: (data) => { setSavedProductId(data.productId); setStep(3); },
        onError: (e: any) => alert(e.message),
    });

    const calcMutation = useMutation({
        mutationFn: () => calculatePremium(savedProductId, { targetSales, lossRatio, salesExpense, adminExpense }),
        onSuccess: (data) => { setCalcResult(data); },
        onError: (e: any) => alert(e.message),
    });

    const toggleCoverage = (id: string) => {
        setSelectedCoverages(prev => prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]);
        if (!selectedCoverages.includes(id)) setSelectedOptions(prev => ({ ...prev, [id]: [] }));
    };
    const toggleOption = (coverageId: string, option: string) =>
        setSelectedOptions(prev => {
            const cur = prev[coverageId] ?? [];
            return { ...prev, [coverageId]: cur.includes(option) ? cur.filter(x => x !== option) : [...cur, option] };
        });
    const toggleRider = (code: string) =>
        setSelectedRiders(prev => prev.includes(code) ? prev.filter(x => x !== code) : [...prev, code]);

    const mandatoryIds = coverages?.filter(c => c.mandatory).map(c => c.coverageId) ?? [];
    const hasMandatory = selectedCoverages.some(id => mandatoryIds.includes(id));

    return (
        <div className="max-w-3xl mx-auto px-6 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">신규 상품 등록</h1>
            <p className="text-slate-500 mb-8">CT-01 ~ CT-03 상품 설계 프로세스</p>

            {/* 진행 단계 */}
            <div className="flex items-center gap-1 mb-10 overflow-x-auto">
                {STEPS.map((label, i) => (
                    <div key={i} className="flex items-center gap-1 shrink-0">
                        <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold
                            ${step === i ? 'bg-slate-900 text-white' : step > i ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-400'}`}>
                            {step > i ? '✓' : i + 1}
                        </div>
                        <span className={`text-xs font-medium whitespace-nowrap ${step === i ? 'text-slate-900' : 'text-gray-400'}`}>{label}</span>
                        {i < STEPS.length - 1 && <div className="w-4 h-px bg-gray-200 shrink-0" />}
                    </div>
                ))}
            </div>

            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8">

                {/* Step 0: 기본정보 */}
                {step === 0 && (
                    <div className="flex flex-col gap-4">
                        <h2 className="text-lg font-bold text-slate-900 mb-2">상품 기본정보 입력</h2>
                        <Field label="상품명" value={productName} onChange={setProductName} placeholder="MZ세대 다이렉트 개인용자동차보험" />
                        <Field label="상품코드" value={productCode} onChange={setProductCode} placeholder="CAR-2026-MZ" />
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">보험종목</label>
                            <select value={target} onChange={e => setTarget(e.target.value)}
                                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                                <option value="PERSONAL">개인용자동차보험</option>
                                <option value="BUSINESS">업무용자동차보험</option>
                                <option value="COMMERCIAL">영업용자동차보험</option>
                            </select>
                        </div>
                        <div className="grid grid-cols-2 gap-3">
                            <Field label="판매시작일" value={saleStartDate} onChange={setSaleStartDate} type="date" />
                            <Field label="판매종료일" value={saleEndDate} onChange={setSaleEndDate} type="date" />
                        </div>
                        <Field label="가입대상 설명" value={description} onChange={setDescription} placeholder="만 20세 이상 39세 이하 운전자" />
                        <button onClick={() => setStep(1)}
                            disabled={!productCode || !productName || !saleStartDate || !saleEndDate}
                            className="w-full py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-300 text-white font-bold rounded-xl mt-2">
                            다음
                        </button>
                    </div>
                )}

                {/* Step 1: 담보 선택 */}
                {step === 1 && (
                    <div className="flex flex-col gap-4">
                        <h2 className="text-lg font-bold text-slate-900 mb-2">담보 선택</h2>
                        <div className="flex flex-col gap-2">
                            {coverages?.map(c => (
                                <div key={c.coverageId} className={`rounded-xl border transition ${selectedCoverages.includes(c.coverageId) ? 'border-blue-400 bg-blue-50' : 'border-gray-200'}`}>
                                    <label className="flex items-center gap-3 p-3 cursor-pointer">
                                        <input type="checkbox" checked={selectedCoverages.includes(c.coverageId)}
                                            onChange={() => toggleCoverage(c.coverageId)} className="accent-blue-600" />
                                        <span className="text-sm font-medium text-slate-900">{c.coverageName}</span>
                                        {c.mandatory && <span className="text-xs text-red-500 font-semibold ml-auto">필수</span>}
                                    </label>
                                    {/* 가입 옵션 선택 */}
                                    {selectedCoverages.includes(c.coverageId) && c.limitOptions.length > 1 && (
                                        <div className="px-4 pb-3 flex flex-wrap gap-2">
                                            {c.limitOptions.map(opt => (
                                                <label key={opt} className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs cursor-pointer border transition
                                                    ${(selectedOptions[c.coverageId] ?? []).includes(opt)
                                                        ? 'bg-blue-600 text-white border-blue-600'
                                                        : 'bg-white text-slate-600 border-slate-200 hover:border-blue-300'}`}>
                                                    <input type="checkbox" className="hidden"
                                                        checked={(selectedOptions[c.coverageId] ?? []).includes(opt)}
                                                        onChange={() => toggleOption(c.coverageId, opt)} />
                                                    {opt}
                                                </label>
                                            ))}
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>
                        {!hasMandatory && selectedCoverages.length > 0 && (
                            <p className="text-xs text-red-500">대인배상 I은 필수 담보입니다.</p>
                        )}
                        <div className="flex gap-2 mt-2">
                            <button onClick={() => setStep(0)} className="flex-1 py-3 border border-gray-200 text-slate-600 font-semibold rounded-xl hover:bg-gray-50">이전</button>
                            <button onClick={() => setStep(2)} disabled={selectedCoverages.length === 0 || !hasMandatory}
                                className="flex-1 py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-300 text-white font-bold rounded-xl">다음</button>
                        </div>
                    </div>
                )}

                {/* Step 2: 특약 선택 */}
                {step === 2 && (
                    <div className="flex flex-col gap-4">
                        <h2 className="text-lg font-bold text-slate-900 mb-2">특약 선택 (선택사항)</h2>
                        <div className="flex flex-col gap-2">
                            {riders?.map(r => (
                                <label key={r.riderCode} className={`flex items-center gap-3 p-3 rounded-xl border cursor-pointer transition
                                    ${selectedRiders.includes(r.riderCode) ? 'border-indigo-400 bg-indigo-50' : 'border-gray-200 hover:border-indigo-200'}`}>
                                    <input type="checkbox" checked={selectedRiders.includes(r.riderCode)}
                                        onChange={() => toggleRider(r.riderCode)} className="accent-indigo-600" />
                                    <span className="text-sm font-medium text-slate-900">{r.riderName}</span>
                                    {r.discountRate > 0 && (
                                        <span className="text-xs text-green-600 font-semibold ml-auto">{(r.discountRate * 100).toFixed(0)}% 할인</span>
                                    )}
                                </label>
                            ))}
                        </div>
                        <div className="flex gap-2 mt-2">
                            <button onClick={() => setStep(1)} className="flex-1 py-3 border border-gray-200 text-slate-600 font-semibold rounded-xl hover:bg-gray-50">이전</button>
                            <button onClick={() => createProduct.mutate()} disabled={createProduct.isPending}
                                className="flex-1 py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-slate-300 text-white font-bold rounded-xl">
                                {createProduct.isPending ? '저장 중...' : '다음 (보험료 산출)'}
                            </button>
                        </div>
                    </div>
                )}

                {/* Step 3: 보험료 산출 (CT-02) */}
                {step === 3 && (
                    <div className="flex flex-col gap-4">
                        <h2 className="text-lg font-bold text-slate-900 mb-2">보험료 산출 (CT-02)</h2>
                        <div className="grid grid-cols-2 gap-3">
                            <NumberField label="목표 판매 건수" value={targetSales} onChange={setTargetSales} />
                            <NumberField label="예상 손해율 (%)" value={lossRatio} onChange={setLossRatio} step={0.1} />
                            <NumberField label="목표 영업비율 (%)" value={salesExpense} onChange={setSalesExpense} step={0.1} />
                            <NumberField label="목표 관리비율 (%)" value={adminExpense} onChange={setAdminExpense} step={0.1} />
                        </div>
                        <button onClick={() => calcMutation.mutate()} disabled={calcMutation.isPending}
                            className="w-full py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white font-bold rounded-xl">
                            {calcMutation.isPending ? '산출 중...' : '수익성 시뮬레이션'}
                        </button>
                        {calcResult && (
                            <div className="bg-slate-50 rounded-xl p-4 text-sm space-y-1.5">
                                <p className="font-semibold text-slate-700 mb-2">산출 결과</p>
                                <Row label="최종 기본 보험료" value={`${calcResult.finalPremium?.toLocaleString()}원`} bold />
                                <Row label="법정 준비금 적립액" value={`${calcResult.reserve?.toLocaleString()}원`} />
                                <Row label="총 수입보험료" value={`${calcResult.totalRevenue?.toLocaleString()}원`} />
                                <Row label="예상 지급보험금" value={`${calcResult.totalClaims?.toLocaleString()}원`} />
                                <Row label="예상 사업비" value={`${calcResult.totalExpenses?.toLocaleString()}원`} />
                                <Row label="예상 영업이익" value={`${calcResult.profit?.toLocaleString()}원`} />
                            </div>
                        )}
                        {calcResult && (
                            <button onClick={() => setStep(4)}
                                className="w-full py-3 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-xl">
                                분석 결과 반영 → 기초서류 등록
                            </button>
                        )}
                    </div>
                )}

                {/* Step 4: 기초서류 등록 (CT-03) */}
                {step === 4 && (
                    <div className="flex flex-col gap-4">
                        <h2 className="text-lg font-bold text-slate-900 mb-2">기초서류 등록 (CT-03)</h2>
                        <p className="text-sm text-slate-500">인가 신청에 필요한 기초서류를 등록하세요.</p>
                        <div className="flex flex-col gap-2">
                            {documents.map((doc, i) => (
                                <div key={i} className="flex items-center gap-3 p-3 rounded-xl border border-gray-200 bg-slate-50">
                                    <span className="text-green-500 text-lg">✓</span>
                                    <span className="text-sm font-medium text-slate-700">{doc}</span>
                                    <span className="text-xs text-slate-400 ml-auto">등록됨</span>
                                </div>
                            ))}
                        </div>
                        <p className="text-xs text-slate-400">* 실제 환경에서는 파일 업로드가 필요합니다.</p>
                        <button onClick={() => setStep(5)}
                            className="w-full py-3 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-xl mt-2">
                            서류 저장 완료 → 상품 설계 완료
                        </button>
                    </div>
                )}

                {/* Step 5: 완료 */}
                {step === 5 && (
                    <div className="text-center py-8">
                        <div className="text-6xl mb-4">✅</div>
                        <h2 className="text-2xl font-extrabold text-slate-900 mb-2">상품 설계 완료</h2>
                        <p className="text-slate-500 mb-2">상품이 성공적으로 등록되었습니다.</p>
                        <p className="text-sm text-blue-600 font-medium mb-8">다음 단계: 상품 관리에서 인가 신청(CT-04) → 요율검증(CT-05) → 판매 확정(CT-06)</p>
                        <button onClick={() => router.push('/employee/products')}
                            className="px-8 py-3 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-xl">
                            상품 관리로 이동
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

function Field({ label, value, onChange, placeholder, type = 'text' }: {
    label: string; value: string; onChange: (v: string) => void; placeholder?: string; type?: string;
}) {
    return (
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <input type={type} value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
    );
}

function NumberField({ label, value, onChange, step = 1 }: { label: string; value: number; onChange: (v: number) => void; step?: number }) {
    return (
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <input type="number" value={value} step={step} onChange={e => onChange(Number(e.target.value))}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
    );
}

function Row({ label, value, bold }: { label: string; value: string; bold?: boolean }) {
    return (
        <div className="flex justify-between">
            <span className="text-slate-500">{label}</span>
            <span className={bold ? 'font-extrabold text-slate-900' : 'text-slate-700'}>{value}</span>
        </div>
    );
}
