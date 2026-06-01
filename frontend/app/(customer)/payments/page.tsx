'use client';

import { Suspense } from "react";
import { useSearchParams } from "next/navigation";
import PaymentWidget from "@/components/payments/PaymentWidget";

function PaymentContent() {
    const params = useSearchParams();
    const subscriptionNo = params.get("subscriptionNo") ?? "";
    const amount = Number(params.get("amount") ?? 0);

    return (
        <main style={{ maxWidth: 560, width: "100%", margin: "0 auto" }}>
            <h1 className="text-xl font-bold mb-2">보험료 결제</h1>
            {subscriptionNo && (
                <p className="text-sm text-gray-500 mb-6">청약번호: {subscriptionNo}</p>
            )}
            <PaymentWidget subscriptionNo={subscriptionNo} amount={amount} />
        </main>
    );
}

export default function PaymentPage() {
    return (
        <Suspense fallback={<div style={{ maxWidth: 560, width: "100%", margin: "0 auto" }}>로딩 중...</div>}>
            <PaymentContent />
        </Suspense>
    );
}
