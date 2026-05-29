'use client';

import { useEffect, useRef, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import {
    getProduct, changeProductStatus,
    addProductDocument, deleteProductDocument, downloadProductDocument,
} from "@/queries/products";

// ── 상수 ──────────────────────────────────────────────────────────────────────
const STATUS_FLOW = [
    "DESIGNING", "KIDI_SUBMITTED", "KIDI_CONFIRMED",
    "FSS_APPLIED", "FSS_APPROVED", "FILING", "FILED", "ON_SALE", "DISCONTINUED",
];

const STATUS_META: Record<string, { label: string; color: string; bg: string }> = {
    DESIGNING:      { label: "설계 중",           color: "text-gray-600",   bg: "bg-gray-100" },
    KIDI_SUBMITTED: { label: "보험개발원 제출",    color: "text-orange-600", bg: "bg-orange-100" },
    KIDI_CONFIRMED: { label: "요율확인서 수령",    color: "text-yellow-700", bg: "bg-yellow-100" },
    FSS_APPLIED:    { label: "금감원 인가신청",    color: "text-blue-600",   bg: "bg-blue-100" },
    FSS_APPROVED:   { label: "금감원 인가완료",    color: "text-indigo-600", bg: "bg-indigo-100" },
    FILING:         { label: "판매신고 중",        color: "text-purple-600", bg: "bg-purple-100" },
    FILED:          { label: "판매 확정",          color: "text-teal-700",   bg: "bg-teal-100" },
    ON_SALE:        { label: "판매 중",            color: "text-green-700",  bg: "bg-green-100" },
    DISCONTINUED:   { label: "판매 중단",          color: "text-red-600",    bg: "bg-red-100" },
};

// 서류 업로드로 자동 전환되는 상태는 수동 버튼 제거
const MANUAL_NEXT: Record<string, string[]> = {
    DESIGNING:      ["KIDI_SUBMITTED"],
    KIDI_SUBMITTED: ["DESIGNING"],       // KIDI_CONFIRMED: 요율확인서 업로드로 자동
    KIDI_CONFIRMED: [],                  // FSS_APPLIED: 보험상품신고서 업로드로 자동
    FSS_APPLIED:    ["FSS_APPROVED"],
    FSS_APPROVED:   ["FILING"],
    FILING:         ["FILED"],
    FILED:          ["ON_SALE"],
    ON_SALE:        ["DISCONTINUED"],
    DISCONTINUED:   [],
};

const AUTO_HINT: Record<string, string> = {
    KIDI_SUBMITTED: "요율확인서 업로드 시 '요율확인서 수령' 단계로 자동 전환됩니다.",
    KIDI_CONFIRMED: "보험상품신고서 업로드 시 '금감원 인가신청' 단계로 자동 전환됩니다.",
};

const DOC_TYPES = [
    { value: "BUSINESS_METHOD",  label: "사업방법서" },
    { value: "TERMS_CONDITIONS", label: "보험약관" },
    { value: "CALC_METHOD",      label: "보험료및책임준비금산출방법서" },
    { value: "RATE_CERT",        label: "요율확인서",     autoNote: "업로드 시 상태 자동 전환" },
    { value: "FSS_APPLICATION",  label: "보험상품신고서", autoNote: "업로드 시 상태 자동 전환" },
];

// ── 컴포넌트 ──────────────────────────────────────────────────────────────────
export default function ProductApprovalPage() {
    const params = useParams();
    const id = params.id as any;

    const [product, setProduct]         = useState<any>(null);
    const [error, setError]             = useState("");
    const [statusLoading, setStatusLoading] = useState(false);
    const [showDocForm, setShowDocForm] = useState(false);
    const [uploading, setUploading]     = useState(false);
    const [docError, setDocError]       = useState("");
    const [downloading, setDownloading] = useState<string | null>(null);

    const [docType, setDocType] = useState("BUSINESS_METHOD");
    const [title, setTitle]     = useState("");
    const [summary, setSummary] = useState("");
    const [note, setNote]       = useState("");
    const fileRef = useRef<HTMLInputElement>(null);

    const load = async () => {
        try {
            const res = await getProduct(id);
            setProduct(res.data ?? res);
        } catch { setError("상품 정보 로드 실패"); }
    };

    useEffect(() => { if (id) load(); }, [id]);

    const resetForm = () => {
        setDocType("BUSINESS_METHOD");
        setTitle(""); setSummary(""); setNote("");
        if (fileRef.current) fileRef.current.value = "";
        setDocError("");
        setShowDocForm(false);
    };

    const handleUpload = async () => {
        if (!title.trim())              { setDocError("제목을 입력하세요."); return; }
        if (!fileRef.current?.files?.[0]) { setDocError("PDF 파일을 선택하세요."); return; }

        const fd = new FormData();
        fd.append("docType",  docType);
        fd.append("title",    title);
        fd.append("summary",  summary);
        fd.append("note",     note);
        fd.append("file",     fileRef.current.files[0]);

        setUploading(true);
        try {
            await addProductDocument(id, fd);
            resetForm();
            await load();
        } catch (e: any) {
            setDocError(e?.message ?? "서류 등록 실패");
        } finally { setUploading(false); }
    };

    const handleStatusChange = async (next: string) => {
        if (!confirm(`상태를 "${STATUS_META[next]?.label}"으로 변경할까요?`)) return;
        setStatusLoading(true);
        try {
            const res = await changeProductStatus(id, next);
            setProduct(res.data ?? res);
        } catch (e: any) {
            alert(e?.message ?? "상태 변경 실패");
        } finally { setStatusLoading(false); }
    };

    const handleDeleteDoc = async (docId: string) => {
        if (!confirm("서류를 삭제할까요?")) return;
        await deleteProductDocument(id, docId);
        await load();
    };

    const handleDownload = async (docId: string, filename: string) => {
        setDownloading(docId);
        try { await downloadProductDocument(id, docId, filename || "document"); }
        catch { alert("다운로드 실패"); }
        finally { setDownloading(null); }
    };

    if (error)    return <p className="text-red-500 text-sm p-6">{error}</p>;
    if (!product) return <p className="text-gray-400 text-sm p-6">로딩 중...</p>;

    const currentIdx   = STATUS_FLOW.indexOf(product.status);
    const nextStatuses = MANUAL_NEXT[product.status] ?? [];
    const sm = STATUS_META[product.status] ?? { label: product.status, color: "text-gray-600", bg: "bg-gray-100" };
    const selectedDocMeta = DOC_TYPES.find(d => d.value === docType);

    return (
        <div className="max-w-4xl space-y-6">

            {/* 헤더 */}
            <div>
                <div className="flex items-center gap-3 mb-1">
                    <Link href={`/employee/products/${id}`}
                        className="text-sm text-gray-400 hover:text-gray-600">← 상품 상세</Link>
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${sm.bg} ${sm.color}`}>
                        {sm.label}
                    </span>
                </div>
                <h1 className="text-xl font-bold text-gray-800">상품인가 관리</h1>
                <p className="text-sm text-gray-500 mt-0.5">{product.productName} · {product.productCode}</p>
            </div>

            {/* 인허가 진행 현황 */}
            <div className="bg-white border border-gray-200 rounded-xl p-6">
                <h2 className="text-sm font-semibold text-gray-600 mb-5">인허가 진행 현황</h2>

                {/* 스텝 바 */}
                <div className="flex items-start overflow-x-auto pb-2">
                    {STATUS_FLOW.map((s, i) => {
                        const meta   = STATUS_META[s];
                        const done   = i < currentIdx;
                        const active = i === currentIdx;
                        return (
                            <div key={s} className="flex items-center shrink-0">
                                <div className={`flex flex-col items-center w-[72px] transition-opacity
                                    ${active ? "opacity-100" : done ? "opacity-80" : "opacity-25"}`}>
                                    <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold
                                        ${active
                                            ? `${meta.bg} ${meta.color} ring-2 ring-offset-2 ring-current`
                                            : done
                                                ? "bg-green-100 text-green-700"
                                                : "bg-gray-100 text-gray-400"}`}>
                                        {done ? "✓" : i + 1}
                                    </div>
                                    <span className={`text-[10px] mt-1.5 text-center leading-tight
                                        ${active ? "font-semibold " + meta.color
                                                 : done ? "text-green-700"
                                                        : "text-gray-400"}`}>
                                        {meta.label}
                                    </span>
                                </div>
                                {i < STATUS_FLOW.length - 1 && (
                                    <div className={`h-0.5 w-3 shrink-0 mb-5
                                        ${i < currentIdx ? "bg-green-400" : "bg-gray-200"}`} />
                                )}
                            </div>
                        );
                    })}
                </div>

                {/* 수동 전환 버튼 */}
                {nextStatuses.length > 0 && (
                    <div className="mt-5 pt-4 border-t border-gray-100 flex items-center gap-3 flex-wrap">
                        <span className="text-xs text-gray-400">수동 전환</span>
                        {nextStatuses.map(next => {
                            const m = STATUS_META[next];
                            return (
                                <button key={next} disabled={statusLoading}
                                    onClick={() => handleStatusChange(next)}
                                    className={`px-4 py-1.5 text-xs rounded-lg font-medium
                                        ${m.bg} ${m.color} hover:opacity-80 disabled:opacity-40`}>
                                    {next === "DESIGNING" ? "← 재설계 (반려)" : `→ ${m.label}`}
                                </button>
                            );
                        })}
                    </div>
                )}

                {/* 자동 전환 안내 */}
                {AUTO_HINT[product.status] && (
                    <p className="mt-3 text-[11px] text-blue-500 flex items-center gap-1">
                        <span>ℹ</span> {AUTO_HINT[product.status]}
                    </p>
                )}
            </div>

            {/* 기초서류 */}
            <div className="bg-white border border-gray-200 rounded-xl p-6">
                <div className="flex items-center justify-between mb-4">
                    <div>
                        <h2 className="text-sm font-semibold text-gray-600">기초서류</h2>
                        <p className="text-xs text-gray-400 mt-0.5">사업방법서, 보험약관, 산출방법서, 요율확인서, 보험상품신고서</p>
                    </div>
                    {!showDocForm && (
                        <button onClick={() => setShowDocForm(true)}
                            className="px-3 py-1.5 text-xs bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                            + 서류 등록
                        </button>
                    )}
                </div>

                {/* 업로드 폼 */}
                {showDocForm && (
                    <div className="mb-5 p-4 bg-gray-50 rounded-xl border border-gray-200 space-y-3">
                        <p className="text-xs font-medium text-gray-700">기초서류 등록 (PDF)</p>
                        {docError && <p className="text-xs text-red-500">{docError}</p>}

                        <div className="grid grid-cols-2 gap-3">
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">서류 종류 *</label>
                                <select value={docType} onChange={e => setDocType(e.target.value)}
                                    className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500">
                                    {DOC_TYPES.map(d => (
                                        <option key={d.value} value={d.value}>{d.label}</option>
                                    ))}
                                </select>
                                {selectedDocMeta?.autoNote && (
                                    <p className="mt-1 text-[11px] text-blue-500">{selectedDocMeta.autoNote}</p>
                                )}
                            </div>
                            <div>
                                <label className="block text-xs text-gray-500 mb-1">제목 *</label>
                                <input value={title} onChange={e => setTitle(e.target.value)}
                                    placeholder="예) 2025년 개인용자동차보험 사업방법서"
                                    className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                            </div>
                            <div className="col-span-2">
                                <label className="block text-xs text-gray-500 mb-1">PDF 파일 *</label>
                                <input ref={fileRef} type="file" accept="application/pdf"
                                    className="w-full text-sm text-gray-600 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
                            </div>
                            <div className="col-span-2">
                                <label className="block text-xs text-gray-500 mb-1">요약</label>
                                <textarea value={summary} onChange={e => setSummary(e.target.value)}
                                    rows={2} placeholder="문서 내용 요약 (선택)"
                                    className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none" />
                            </div>
                            <div className="col-span-2">
                                <label className="block text-xs text-gray-500 mb-1">비고</label>
                                <input value={note} onChange={e => setNote(e.target.value)}
                                    placeholder="기타 메모 (선택)"
                                    className="w-full text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                            </div>
                        </div>

                        <div className="flex justify-end gap-2 pt-1">
                            <button onClick={resetForm} disabled={uploading}
                                className="px-3 py-1.5 text-sm text-gray-500 border border-gray-300 rounded-lg hover:bg-gray-100 disabled:opacity-40">
                                취소
                            </button>
                            <button onClick={handleUpload} disabled={uploading}
                                className="px-4 py-1.5 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-40">
                                {uploading ? "업로드 중..." : "등록"}
                            </button>
                        </div>
                    </div>
                )}

                {/* 서류 목록 */}
                {(!product.documents || product.documents.length === 0) ? (
                    <p className="text-sm text-gray-400">등록된 기초서류가 없습니다.</p>
                ) : (
                    <table className="w-full text-xs">
                        <thead>
                            <tr className="text-gray-400 border-b border-gray-100">
                                <th className="pb-2 text-left font-medium">종류</th>
                                <th className="pb-2 text-left font-medium">제목</th>
                                <th className="pb-2 text-left font-medium">제출일</th>
                                <th className="pb-2 text-left font-medium">수령일</th>
                                <th className="pb-2 text-left font-medium">요약</th>
                                <th className="pb-2 text-right font-medium">파일</th>
                                <th className="pb-2"></th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-50">
                            {product.documents.map((doc: any) => {
                                const meta  = DOC_TYPES.find(d => d.value === doc.docType);
                                const docId = doc._id ?? doc.id;
                                return (
                                    <tr key={docId} className="hover:bg-gray-50">
                                        <td className="py-2">
                                            <span className="px-1.5 py-0.5 bg-gray-100 text-gray-600 rounded text-[10px] whitespace-nowrap">
                                                {meta?.label ?? doc.docType}
                                            </span>
                                        </td>
                                        <td className="py-2 font-medium max-w-[140px] truncate">{doc.title}</td>
                                        <td className="py-2 text-gray-400 whitespace-nowrap">{doc.submittedAt || "-"}</td>
                                        <td className="py-2 text-gray-400 whitespace-nowrap">{doc.receivedAt || "-"}</td>
                                        <td className="py-2 text-gray-400 max-w-[160px] truncate">{doc.summary}</td>
                                        <td className="py-2 text-right">
                                            {doc.filename ? (
                                                <button
                                                    disabled={downloading === docId}
                                                    onClick={() => handleDownload(docId, doc.filename)}
                                                    className="text-blue-500 hover:text-blue-700 disabled:opacity-40 whitespace-nowrap">
                                                    {downloading === docId ? "..." : "↓ PDF"}
                                                </button>
                                            ) : (
                                                <span className="text-gray-300">없음</span>
                                            )}
                                        </td>
                                        <td className="py-2 pl-3 text-right">
                                            <button onClick={() => handleDeleteDoc(docId)}
                                                className="text-red-400 hover:text-red-600">삭제</button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}
