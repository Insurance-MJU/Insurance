'use client';
import { useState, Suspense } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { fetchApi } from '@/queries/api';
import { estimatePremium } from '@/queries/products';
import { createSubscription } from '@/queries/subscriptions';

function ApplyForm() {
    const searchParams = useSearchParams();
    const productId = searchParams.get('productId') ?? '';
    const router = useRouter();

    const [step, setStep] = useState(1);

    // Step 1 - 본인인증
    const [name, setName] = useState('');
    const [ssn, setSsn] = useState('');
    const [phone, setPhone] = useState('');
    const [sessionId, setSessionId] = useState('');
    const [otp, setOtp] = useState('');
    const [otpSent, setOtpSent] = useState(false);
    const [verificationToken, setVerificationToken] = useState('');

    // Step 2 - 차량 정보
    const [carNumber, setCarNumber] = useState('');
    const [chassisNumber, setChassisNumber] = useState('');
    const [address, setAddress] = useState('');
    const [occupation, setOccupation] = useState('');
    const [carPurpose, setCarPurpose] = useState('COMMUTE');
    const [driverScope, setDriverScope] = useState('ALL');
    const [vehicle, setVehicle] = useState<any>(null);

    // Step 3 - 보험료
    const [premium, setPremium] = useState<number | null>(null);

    const [loading, setLoading] = useState(false);

    // ── Step 1: OTP 발송 ──────────────────────────────────────
    const sendOtp = async () => {
        setLoading(true);
        try {
            const res = await fetchApi('/verification/send-otp', {
                method: 'POST',
                body: JSON.stringify({ name, ssn, phone }),
            });
            setSessionId(res.sessionId);
            setOtpSent(true);
            alert('인증번호가 발송됐습니다. (테스트: 123456)');
        } catch (e: any) {
            alert(e.message);
        } finally {
            setLoading(false);
        }
    };

    const verifyOtp = async () => {
        setLoading(true);
        try {
            const res = await fetchApi('/verification/verify-otp', {
                method: 'POST',
                body: JSON.stringify({ sessionId, otp }),
            });
            if (!res.success) { alert(res.errorMessage); return; }
            setVerificationToken(res.verificationToken);
            setStep(2);
        } catch (e: any) {
            alert(e.message);
        } finally {
            setLoading(false);
        }
    };

    // ── Step 2: 차량 조회 후 보험료 계산 ─────────────────────
    const lookupVehicle = async () => {
        setLoading(true);
        try {
            const res = await fetchApi(`/vehicles/${encodeURIComponent(carNumber)}`, {});
            if (res.failureReason) { alert(res.failureReason); return; }
            setVehicle(res);

            const est = await estimatePremium(productId, {
                carStandardValue: res.standardValue,
                carPurpose,
            });
            setPremium(est.finalPremium);
            setStep(3);
        } catch (e: any) {
            alert(e.message);
        } finally {
            setLoading(false);
        }
    };

    // ── Step 4: 최종 신청 ─────────────────────────────────────
    const submit = async () => {
        setLoading(true);
        try {
            await createSubscription({
                verificationToken,
                productId,
                address,
                carNumber,
                chassisNumber,
                occupation,
                premium: premium!,
            });
            setStep(4);
        } catch (e: any) {
            alert(e.message);
        } finally {
            setLoading(false);
        }
    };

    if (step === 4) {
        return (
            <div className="text-center py-20">
                <div className="text-6xl mb-6">✅</div>
                <h2 className="text-2xl font-extrabold text-slate-900 mb-2">가입 신청 완료</h2>
                <p className="text-slate-500 mb-8">심사 후 결과를 안내해 드립니다.</p>
                <button onClick={() => router.push('/insurance/contracts')}
                    className="px-6 py-3 bg-slate-900 text-white font-bold rounded-xl hover:bg-slate-800 transition">
                    내 계약 확인
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-lg mx-auto px-4 py-12">
            {/* 진행 단계 */}
            <div className="flex items-center gap-2 mb-10">
                {['본인인증', '차량정보', '보험료확인', '신청완료'].map((label, i) => (
                    <div key={i} className="flex items-center gap-2 flex-1">
                        <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0
                            ${step === i + 1 ? 'bg-slate-900 text-white' : step > i + 1 ? 'bg-blue-500 text-white' : 'bg-gray-100 text-gray-400'}`}>
                            {step > i + 1 ? '✓' : i + 1}
                        </div>
                        <span className={`text-xs font-medium ${step === i + 1 ? 'text-slate-900' : 'text-gray-400'}`}>{label}</span>
                        {i < 3 && <div className="flex-1 h-px bg-gray-200" />}
                    </div>
                ))}
            </div>

            <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-8 flex flex-col gap-5">

                {/* ── Step 1 ── */}
                {step === 1 && (
                    <>
                        <h2 className="text-xl font-extrabold text-slate-900">본인 인증</h2>
                        <Field label="이름" value={name} onChange={setName} placeholder="홍길동" />
                        <Field label="주민등록번호" value={ssn} onChange={setSsn} placeholder="900101-1234567" />
                        <Field label="휴대폰 번호" value={phone} onChange={setPhone} placeholder="010-0000-0000" />
                        {!otpSent ? (
                            <button onClick={sendOtp} disabled={loading || !name || !ssn || !phone}
                                className="w-full py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 text-white font-bold rounded-xl transition">
                                인증번호 발송
                            </button>
                        ) : (
                            <>
                                <Field label="인증번호" value={otp} onChange={setOtp} placeholder="123456" />
                                <button onClick={verifyOtp} disabled={loading || !otp}
                                    className="w-full py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-gray-300 text-white font-bold rounded-xl transition">
                                    {loading ? '확인 중...' : '인증 확인'}
                                </button>
                            </>
                        )}
                    </>
                )}

                {/* ── Step 2 ── */}
                {step === 2 && (
                    <>
                        <h2 className="text-xl font-extrabold text-slate-900">차량 정보</h2>
                        <Field label="차량 번호" value={carNumber} onChange={setCarNumber} placeholder="12가 3456" />
                        <Field label="차대 번호" value={chassisNumber} onChange={setChassisNumber} placeholder="KMHXX00XXXX000000" />
                        <Field label="주소" value={address} onChange={setAddress} placeholder="서울시 강남구 테헤란로 123" />
                        <Field label="직업" value={occupation} onChange={setOccupation} placeholder="회사원" />
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">차량 용도</label>
                            <select value={carPurpose} onChange={e => setCarPurpose(e.target.value)}
                                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                                <option value="COMMUTE">출퇴근/가정용</option>
                                <option value="BUSINESS">업무용</option>
                                <option value="COMMERCIAL">영업용</option>
                            </select>
                        </div>
                        <button onClick={lookupVehicle} disabled={loading || !carNumber || !chassisNumber || !address || !occupation}
                            className="w-full py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-gray-300 text-white font-bold rounded-xl transition">
                            {loading ? '조회 중...' : '차량 조회 및 보험료 계산'}
                        </button>
                    </>
                )}

                {/* ── Step 3 ── */}
                {step === 3 && vehicle && (
                    <>
                        <h2 className="text-xl font-extrabold text-slate-900">보험료 확인</h2>
                        <div className="bg-slate-50 rounded-xl p-4 text-sm space-y-1.5">
                            <p className="font-semibold text-slate-700">{vehicle.manufacturer} {vehicle.modelName} ({vehicle.modelYear})</p>
                            <p className="text-slate-500">차량 기준가액: {vehicle.standardValue?.toLocaleString()}원</p>
                            <p className="text-slate-500">차량번호: {vehicle.carNumber}</p>
                        </div>
                        <div className="text-center py-4">
                            <p className="text-sm text-slate-500 mb-1">월 납입 보험료</p>
                            <p className="text-4xl font-extrabold text-slate-900">{premium?.toLocaleString()}원</p>
                        </div>
                        <button onClick={submit} disabled={loading}
                            className="w-full py-3 bg-slate-900 hover:bg-slate-800 disabled:bg-gray-300 text-white font-bold rounded-xl transition">
                            {loading ? '신청 중...' : '가입 신청하기'}
                        </button>
                        <button onClick={() => setStep(2)} className="w-full py-2 text-sm text-slate-400 hover:text-slate-600">
                            ← 이전으로
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}

function Field({ label, value, onChange, placeholder }: {
    label: string; value: string; onChange: (v: string) => void; placeholder?: string;
}) {
    return (
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <input type="text" value={value} onChange={e => onChange(e.target.value)} placeholder={placeholder}
                className="w-full border border-gray-200 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
    );
}

export default function ApplyPage() {
    return (
        <Suspense>
            <ApplyForm />
        </Suspense>
    );
}
