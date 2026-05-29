'use client';

import { useState } from "react";
import { useTossPayment } from "@/hooks/useTossPayment";

export default function PaymentWidget() {
    type PaymentAmount = { currency: string; value: number };
    const [amount] = useState<PaymentAmount>({ currency: "KRW", value: 50_000 });
    const { ready, requestPayment } = useTossPayment(amount);

    const handlePayment = async () => {
        try {
            await requestPayment("ORDER_" + Date.now(), "보험 상품 가입");
        } catch (error: unknown) {
            const e = error as { code?: string; message?: string };
            if (e?.code === "USER_CANCEL") return;
            alert(`결제 오류: ${e?.message ?? "알 수 없는 오류"}`);
        }
    };

    return (
        <div style={{ width: "100%" }}>
            <div id="payment-method" style={{ width: "100%" }} />
            <div id="agreement" style={{ width: "100%" }} />
            <button disabled={!ready} onClick={handlePayment} suppressHydrationWarning={true}>결제하기</button>
        </div>
    );
}
