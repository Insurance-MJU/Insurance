'use client';

import { Suspense } from "react";
import { useSearchParams } from "next/navigation";
import Link from "next/link";

const MESSAGES: Record<string, string> = {
    PAY_PROCESS_CANCELED:                              "결제를 취소하셨습니다.",
    PAY_PROCESS_ABORTED:                               "결제 진행 중 오류가 발생했습니다.",
    REJECT_CARD_COMPANY:                               "카드사에서 승인을 거절했습니다. 다른 결제수단을 이용해 주세요.",
    CONFIRM_FAILED:                                    "결제 승인에 실패했습니다. 다시 시도해 주세요.",
    TOSS_QUICK_PAY_AMOUNT_LIMIT_EXCEEDED_WITH_UNAVAILABLE_BANKPAY: "퀵계좌이체 한도를 초과했습니다. 카드 결제를 이용해 주세요.",
};

function FailContent() {
    const searchParams = useSearchParams();
    const code           = searchParams.get("code") ?? "";
    const message        = searchParams.get("message") ?? "";
    const subscriptionNo = searchParams.get("subscriptionNo") ?? "";
    const amount         = searchParams.get("amount") ?? "";

    const displayMessage = MESSAGES[code] ?? message;
    const isCanceled     = code === "PAY_PROCESS_CANCELED";
    const retryUrl       = subscriptionNo
        ? `/payments?subscriptionNo=${subscriptionNo}&amount=${amount}`
        : "/insurance/contracts";

    return (
        <div style={{ maxWidth: 480, width: "100%", margin: "0 auto" }}>
            <h2 className="text-xl font-bold mb-4">{isCanceled ? "결제 취소" : "결제 실패"}</h2>
            <p className="text-gray-600">{displayMessage}</p>
            {!isCanceled && <p className="text-sm text-gray-400 mt-1">에러 코드: {code}</p>}
            <div className="flex gap-3 mt-6">
                <Link href={retryUrl}
                    className="px-5 py-2.5 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 text-sm">
                    다시 결제하기
                </Link>
                <Link href="/insurance/contracts"
                    className="px-5 py-2.5 bg-gray-100 text-gray-700 font-semibold rounded-lg hover:bg-gray-200 text-sm">
                    내 보험 현황
                </Link>
            </div>
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
