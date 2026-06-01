'use client';

import { useEffect, useState } from "react";
import { useTossPayment } from "@/hooks/useTossPayment";
import { fetchApi } from "@/queries/api";

interface Props {
    subscriptionNo: string;
    amount: number;
}

export default function PaymentWidget({ subscriptionNo, amount }: Props) {
    const [orderId, setOrderId] = useState<string | null>(null);
    const [preparing, setPreparing] = useState(true);
    const [prepareError, setPrepareError] = useState("");

    const tossAmount = { currency: "KRW", value: amount };
    const { ready, requestPayment } = useTossPayment(tossAmount);

    useEffect(() => {
        if (!subscriptionNo || !amount) { setPreparing(false); return; }
        fetchApi("/payments/prepare", {
            method: "POST",
            body: JSON.stringify({ subscriptionNo, amount }),
        })
            .then(res => setOrderId(res.orderId))
            .catch(() => setPrepareError("결제 준비에 실패했습니다. 잠시 후 다시 시도해 주세요."))
            .finally(() => setPreparing(false));
    }, [subscriptionNo, amount]);

    const handlePayment = async () => {
        if (!orderId) return;
        try {
            await requestPayment(orderId, "보험 초회보험료", { subscriptionNo, amount });
        } catch (error: unknown) {
            const e = error as { code?: string; message?: string };
            if (e?.code === "USER_CANCEL") return;
            alert(`결제 오류: ${e?.message ?? "알 수 없는 오류"}`);
        }
    };

    if (preparing) return <p className="text-sm text-gray-400">결제 준비 중...</p>;
    if (prepareError) return <p className="text-sm text-red-500">{prepareError}</p>;

    return (
        <div style={{ width: "100%" }}>
            <div className="mb-4 p-4 bg-gray-50 rounded-lg text-sm">
                <p className="font-semibold">납부 금액</p>
                <p className="text-2xl font-bold text-blue-600 mt-1">{amount.toLocaleString()}원</p>
            </div>
            <div id="payment-method" style={{ width: "100%" }} />
            <div id="agreement" style={{ width: "100%" }} />
            <button
                disabled={!ready || !orderId}
                onClick={handlePayment}
                className="w-full mt-4 py-3 bg-blue-600 text-white font-bold rounded-lg hover:bg-blue-700 disabled:opacity-40"
                suppressHydrationWarning
            >
                {ready ? "결제하기" : "결제 위젯 로딩 중..."}
            </button>
        </div>
    );
}
