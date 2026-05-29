'use client';

import { Suspense, useEffect, useState } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";
import { confirmPayment } from "@/lib/payments/toss";

type Status = "pending" | "success" | "failed";

function SuccessContent() {
    const searchParams = useSearchParams();
    const router = useRouter();
    const [status, setStatus] = useState<Status>("pending");

    const paymentKey = searchParams.get("paymentKey");
    const orderId = searchParams.get("orderId");
    const amount = searchParams.get("amount");

    useEffect(() => {
        if (!paymentKey) return;
        confirmPayment({ paymentKey, orderId: orderId!, amount: amount! })
            .then(() => setStatus("success"))
            .catch(() => {
                setStatus("failed");
                router.push(`/payments/fail?message=승인실패&code=CONFIRM_FAILED`);
            });
    }, [paymentKey]);

    if (status === "pending") {
        return <div style={{ maxWidth: 480, width: "100%", margin: "0 auto" }}>결제 승인 중...</div>;
    }

    if (status === "failed") {
        return null;
    }

    return (
        <div style={{ maxWidth: 480, width: "100%", margin: "0 auto" }}>
            <h2 className="text-xl font-bold mb-4">결제 성공</h2>
            <p>주문번호: {orderId}</p>
            <p>결제 금액: {Number(amount).toLocaleString()}원</p>
            <p className="text-sm text-gray-400 mt-1">paymentKey: {paymentKey}</p>
            <Link href="/" className="inline-block mt-6 text-blue-500 underline">홈으로 돌아가기</Link>
        </div>
    );
}

export default function SuccessPage() {
    return (
        <Suspense fallback={<div style={{ maxWidth: 480, width: "100%", margin: "0 auto" }}>로딩 중...</div>}>
            <SuccessContent />
        </Suspense>
    );
}
