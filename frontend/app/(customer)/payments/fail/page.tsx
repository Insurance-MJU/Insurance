'use client';

import { Suspense } from "react";
import { useSearchParams } from "next/navigation";
import Link from "next/link";

const MESSAGES: Record<string, string> = {
    PAY_PROCESS_CANCELED: "결제를 취소하셨습니다.",
    PAY_PROCESS_ABORTED:  "결제 진행 중 오류가 발생했습니다.",
    REJECT_CARD_COMPANY:  "카드사에서 승인을 거절했습니다. 다른 결제수단을 이용해 주세요.",
};

function FailContent() {
    const searchParams = useSearchParams();
    const code = searchParams.get("code") ?? "";
    const message = searchParams.get("message") ?? "";

    const displayMessage = MESSAGES[code] ?? message;
    const isCanceled = code === "PAY_PROCESS_CANCELED";

    return (
        <div style={{ maxWidth: 480, width: "100%", margin: "0 auto" }}>
            <h2 className="text-xl font-bold mb-4">{isCanceled ? "결제 취소" : "결제 실패"}</h2>
            <p className="text-gray-600">{displayMessage}</p>
            {!isCanceled && <p className="text-sm text-gray-400 mt-1">에러 코드: {code}</p>}
            <Link href="/payments" className="inline-block mt-6 text-blue-500 underline">다시 결제하기</Link>
        </div>
    );
}

export default function FailPage() {
    return (
        <Suspense fallback={null}>
            <FailContent />
        </Suspense>
    );
}
