'use client';

import { Suspense, useEffect, useRef, useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { fetchApi } from '@/queries/api';
import { fetchMyContracts } from '@/queries/contracts';

function ClaimsContent() {
    const searchParams   = useSearchParams();
    const router         = useRouter();
    const type           = searchParams.get('type') === 'payout' ? '보험금 청구' : '사고 접수';
    const preselectedCid = searchParams.get('cid') || '';

    const [step, setStep]               = useState<1 | 2>(1);
    const [contracts, setContracts]     = useState<any[]>([]);
    const [selectedCid, setSelectedCid] = useState(preselectedCid);
    const [phone, setPhone]             = useState("");
    const [accidentDate, setAccidentDate]         = useState("");
    const [accidentTime, setAccidentTime]         = useState("");
    const [accidentLocation, setAccidentLocation] = useState("");
    const [accidentDetail, setAccidentDetail]     = useState("");

    const diagnosisRef = useRef<HTMLInputElement>(null);
    const sceneRef     = useRef<HTMLInputElement>(null);
    const [diagnosisFile, setDiagnosisFile] = useState<File | null>(null);
    const [sceneFile,     setSceneFile]     = useState<File | null>(null);
    const [consent,       setConsent]       = useState(false);

    const [loading,   setLoading]   = useState(false);
    const [submitted, setSubmitted] = useState<string | null>(null);

    useEffect(() => {
        fetchMyContracts()
            .then(data => setContracts(data))
            .catch(() => setContracts([]));
    }, []);

    const handleStep1 = (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedCid) { alert("계약을 선택하세요."); return; }
        setStep(2);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!diagnosisFile || !sceneFile) { alert("진단서와 현장사진을 첨부해 주세요."); return; }
        if (!consent) { alert("최종 제출에 동의해 주세요."); return; }
        setLoading(true);
        try {
            const selectedContract = contracts.find(c => c.id === selectedCid || c.policyNo === selectedCid);
            const docNames = [diagnosisFile.name, sceneFile.name].join(", ");
            const res = await fetchApi('/accidents', {
                method: 'POST',
                body: JSON.stringify({
                    reportedBy:       selectedContract?.insuredName ?? "고객",
                    phone,
                    accidentDate:     `${accidentDate} ${accidentTime}`,
                    accidentLocation,
                    accidentDetail,
                    documents:        docNames,
                    contractId:       selectedContract?.policyNo ?? selectedCid,
                }),
            });
            const acc = res.data ?? res;
            setSubmitted(acc.accidentId ?? "접수완료");
        } catch (e: any) {
            alert(e?.message ?? "접수 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    if (submitted) {
        return (
            <main className="max-w-xl mx-auto p-4 text-center mt-12">
                <div className="text-4xl mb-4">✅</div>
                <h1 className="text-2xl font-bold mb-2">보험금 청구 완료</h1>
                <p className="text-gray-500 mb-1">부여된 접수 번호</p>
                <p className="font-mono text-blue-600 text-lg mb-6">{submitted}</p>
                <p className="text-sm text-gray-400 mb-6">보상팀에 이관되었습니다. 담당자 배정 후 연락드립니다.</p>
                <button onClick={() => router.push('/insurance/contracts')}
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm">
                    계약 목록으로
                </button>
            </main>
        );
    }

    return (
        <main className="max-w-xl mx-auto p-4">
            <h1 className="text-2xl font-bold mb-2">{type} 신청</h1>

            {/* 단계 표시 */}
            <div className="flex items-center gap-3 mb-6">
                {["사고 정보 입력", "서류 제출"].map((s, i) => (
                    <div key={s} className="flex items-center gap-2">
                        <div className={`w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold
                            ${step === i + 1 ? 'bg-blue-600 text-white'
                            : step > i + 1  ? 'bg-green-500 text-white'
                            : 'bg-gray-200 text-gray-500'}`}>
                            {step > i + 1 ? '✓' : i + 1}
                        </div>
                        <span className={`text-sm ${step === i + 1 ? 'font-medium text-gray-800' : 'text-gray-400'}`}>{s}</span>
                        {i < 1 && <div className="h-0.5 w-8 bg-gray-200" />}
                    </div>
                ))}
            </div>

            {/* 1단계: 사고 정보 입력 */}
            {step === 1 && (
                <form onSubmit={handleStep1} className="flex flex-col gap-4">
                    <div>
                        <label className="block text-sm font-semibold mb-1">대상 보험계약 *</label>
                        {contracts.length > 0 ? (
                            <select value={selectedCid} onChange={e => setSelectedCid(e.target.value)}
                                required className="w-full border p-2 rounded text-sm">
                                <option value="">계약을 선택하세요</option>
                                {contracts.map(c => (
                                    <option key={c.id} value={c.id}>
                                        {c.productName} ({c.policyNo || c.id})
                                    </option>
                                ))}
                            </select>
                        ) : (
                            <input type="text" readOnly value={preselectedCid || "계약 없음"}
                                className="w-full border p-2 bg-gray-100 rounded text-sm" />
                        )}
                    </div>
                    <div>
                        <label className="block text-sm font-semibold mb-1">연락처 *</label>
                        <input type="tel" required value={phone} onChange={e => setPhone(e.target.value)}
                            placeholder="010-1234-5678" className="w-full border p-2 rounded text-sm" />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold mb-1">사고 발생 일시 *</label>
                        <div className="flex gap-2">
                            <input type="date" required value={accidentDate}
                                onChange={e => setAccidentDate(e.target.value)}
                                className="flex-1 border p-2 rounded text-sm" />
                            <input type="time" required value={accidentTime}
                                onChange={e => setAccidentTime(e.target.value)}
                                className="w-32 border p-2 rounded text-sm" />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-semibold mb-1">사고 장소 *</label>
                        <input type="text" required value={accidentLocation}
                            onChange={e => setAccidentLocation(e.target.value)}
                            placeholder="예: 서울시 강남구" className="w-full border p-2 rounded text-sm" />
                    </div>
                    <div>
                        <label className="block text-sm font-semibold mb-1">
                            {type === '사고 접수' ? '사고 발생 경위 *' : '청구 사유 및 금액 *'}
                        </label>
                        <textarea required value={accidentDetail}
                            onChange={e => setAccidentDetail(e.target.value)}
                            className="w-full border p-2 rounded h-28 text-sm"
                            placeholder={type === '사고 접수'
                                ? "사고 경위를 상세히 적어주세요."
                                : "진단명, 수술여부, 청구 금액 등을 적어주세요."} />
                    </div>
                    <button type="submit"
                        className="w-full bg-blue-500 text-white p-3 rounded font-bold hover:bg-blue-600 text-sm">
                        다음: 서류 제출 →
                    </button>
                </form>
            )}

            {/* 2단계: 서류 제출 */}
            {step === 2 && (
                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <div className="border border-gray-200 rounded-xl p-4 bg-gray-50 space-y-4">
                        <p className="text-sm font-semibold text-gray-700">필수 제출 서류</p>
                        <div>
                            <label className="block text-sm font-medium mb-1.5 text-gray-600">
                                진단서 *
                                <span className="text-xs text-gray-400 font-normal ml-1">(jpg, png, pdf)</span>
                            </label>
                            <input ref={diagnosisRef} type="file" accept=".jpg,.jpeg,.png,.pdf"
                                onChange={e => setDiagnosisFile(e.target.files?.[0] ?? null)}
                                className="w-full text-sm text-gray-600 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
                            {diagnosisFile && (
                                <p className="text-xs text-green-600 mt-1">✓ {diagnosisFile.name}</p>
                            )}
                        </div>
                        <div>
                            <label className="block text-sm font-medium mb-1.5 text-gray-600">
                                현장사진 *
                                <span className="text-xs text-gray-400 font-normal ml-1">(jpg, png)</span>
                            </label>
                            <input ref={sceneRef} type="file" accept=".jpg,.jpeg,.png"
                                onChange={e => setSceneFile(e.target.files?.[0] ?? null)}
                                className="w-full text-sm text-gray-600 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
                            {sceneFile && (
                                <p className="text-xs text-green-600 mt-1">✓ {sceneFile.name}</p>
                            )}
                        </div>
                        {(!diagnosisFile || !sceneFile) && (
                            <p className="text-xs text-gray-400">※ 진단서 및 현장사진은 필수 첨부 서류입니다.</p>
                        )}
                    </div>

                    <label className="flex items-start gap-2 text-sm cursor-pointer">
                        <input type="checkbox" checked={consent} onChange={e => setConsent(e.target.checked)}
                            className="mt-0.5 rounded" />
                        <span className="text-gray-700">위 청구 내용 및 제출 서류가 사실임을 확인하며 최종 제출에 동의합니다.</span>
                    </label>

                    <div className="flex gap-3">
                        <button type="button" onClick={() => setStep(1)}
                            className="flex-1 border border-gray-300 text-gray-600 p-3 rounded font-medium text-sm hover:bg-gray-50">
                            ← 이전
                        </button>
                        <button type="submit"
                            disabled={loading || !diagnosisFile || !sceneFile || !consent}
                            className="flex-1 bg-blue-500 text-white p-3 rounded font-bold hover:bg-blue-600 disabled:opacity-50 text-sm">
                            {loading ? "접수 중..." : "최종 청구 완료"}
                        </button>
                    </div>
                </form>
            )}
        </main>
    );
}

export default function ClaimsPage() {
    return (
        <Suspense fallback={null}>
            <ClaimsContent />
        </Suspense>
    );
}
