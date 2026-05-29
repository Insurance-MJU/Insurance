'use client';

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { fetchApi } from "@/queries/api";

export default function InvestigationPage() {
    const { id } = useParams();
    const router = useRouter();
    const [existing, setExisting] = useState<any>(null);
    const [form, setForm] = useState({
        opinion: "",
        damageCode: "CAR-D-01",
        injuryGrade: 12,
        ourFault: 0,
        otherFault: 100,
        liability: "부책",
        finalOpinion: "",
    });
    const [loading, setLoading] = useState(false);
    const [saved, setSaved] = useState(false);

    useEffect(() => {
        if (!id) return;
        fetchApi(`/accidents/${id}/investigation`)
            .then(r => {
                const data = r.data ?? r;
                setExisting(data);
                if (data.exists) {
                    setForm(f => ({
                        ...f,
                        damageCode:   data.damageCode   ?? f.damageCode,
                        liability:    data.liability    ?? f.liability,
                    }));
                }
            })
            .catch(() => {});
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (Number(form.ourFault) + Number(form.otherFault) !== 100) {
            alert("과실비율 합계가 100%여야 합니다.");
            return;
        }
        setLoading(true);
        try {
            await fetchApi(`/accidents/${id}/investigation`, {
                method: "POST",
                body: JSON.stringify({
                    opinion:      form.opinion,
                    damageCode:   form.damageCode,
                    injuryGrade:  Number(form.injuryGrade),
                    ourFault:     Number(form.ourFault),
                    otherFault:   Number(form.otherFault),
                    liability:    form.liability,
                    finalOpinion: form.finalOpinion,
                }),
            });
            setSaved(true);
            alert("손해 조사 내역이 저장되었습니다.");
        } catch (e: any) {
            alert(e?.message ?? "저장 실패");
        } finally {
            setLoading(false);
        }
    };

    const set = (field: string) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) =>
        setForm(f => ({ ...f, [field]: e.target.value }));

    return (
        <div className="max-w-3xl space-y-6">
            <div>
                <Link href={`/employee/accidents/${id}`} className="text-sm text-gray-400 hover:text-gray-600">← 사고 상세</Link>
                <h1 className="text-xl font-bold text-gray-800 mt-1">손해 조사 (CL-03)</h1>
                <p className="text-xs text-gray-400">사고번호: {id}</p>
            </div>

            {existing?.exists && !saved && (
                <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-4 text-sm text-yellow-700">
                    ⚠️ 기존 조사 내역이 있습니다 (저장일: {existing.savedAt}). 아래 내용으로 덮어씁니다.
                </div>
            )}

            {saved ? (
                <div className="bg-green-50 border border-green-200 rounded-xl p-6 text-center">
                    <p className="text-green-700 font-semibold text-lg">조사 완료 및 저장 완료</p>
                    <p className="text-sm text-gray-500 mt-1">손해액 산정 단계로 진행하세요.</p>
                    <div className="flex gap-3 justify-center mt-4">
                        <Link href={`/employee/accidents/${id}`}
                            className="px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50">
                            사고 상세로
                        </Link>
                        <Link href="/employee/claims"
                            className="px-4 py-2 text-sm text-white bg-blue-600 rounded-lg hover:bg-blue-700">
                            손해 처리 목록으로
                        </Link>
                    </div>
                </div>
            ) : (
                <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-6 space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="col-span-2">
                            <label className="block text-xs text-gray-500 mb-1">현장 소견 *</label>
                            <textarea value={form.opinion} onChange={set('opinion')} required rows={3}
                                placeholder="블랙박스 분석 결과, 충격 부위, 파손 형상 등"
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">파손 부위 코드 *</label>
                            <input value={form.damageCode} onChange={set('damageCode')} required
                                placeholder="예: CAR-D-03"
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">부상 급수 (1~14)</label>
                            <input type="number" min={1} max={14} value={form.injuryGrade}
                                onChange={set('injuryGrade')}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">당사 과실 (%)</label>
                            <input type="number" min={0} max={100} value={form.ourFault}
                                onChange={set('ourFault')}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">타사 과실 (%)</label>
                            <input type="number" min={0} max={100} value={form.otherFault}
                                onChange={set('otherFault')}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm" />
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">면/부책 판정</label>
                            <select value={form.liability} onChange={set('liability')}
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm">
                                <option value="부책">부책</option>
                                <option value="면책">면책</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-xs text-gray-500 mb-1">과실비율 합계</label>
                            <p className={`text-sm font-semibold mt-2 ${Number(form.ourFault) + Number(form.otherFault) === 100 ? 'text-green-600' : 'text-red-500'}`}>
                                {Number(form.ourFault) + Number(form.otherFault)}% {Number(form.ourFault) + Number(form.otherFault) === 100 ? '✓' : '(100% 맞춰주세요)'}
                            </p>
                        </div>
                        <div className="col-span-2">
                            <label className="block text-xs text-gray-500 mb-1">최종 조사 의견 *</label>
                            <textarea value={form.finalOpinion} onChange={set('finalOpinion')} required rows={3}
                                placeholder="합의금 산출 진행 요망 등"
                                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none" />
                        </div>
                    </div>
                    <div className="flex gap-3 pt-2">
                        <Link href={`/employee/accidents/${id}`}
                            className="px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded-lg hover:bg-gray-50">
                            취소
                        </Link>
                        <button type="submit" disabled={loading}
                            className="px-6 py-2 bg-green-600 text-white text-sm font-semibold rounded-lg hover:bg-green-700 disabled:opacity-40">
                            {loading ? "저장 중..." : "조사 완료 및 저장"}
                        </button>
                    </div>
                </form>
            )}
        </div>
    );
}
