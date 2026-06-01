'use client';

import { loadTossPayments, type TossPaymentsWidgets, type WidgetPaymentMethodWidget, type WidgetAgreementWidget } from "@tosspayments/tosspayments-sdk";
import { useEffect, useRef, useState } from "react";

const clientKey = process.env.NEXT_PUBLIC_TOSS_CLIENT_KEY!;
const customerKey = process.env.NEXT_PUBLIC_TOSS_CUSTOMER_KEY!;

export function useTossPayment(amount: { currency: string; value: number }, enabled = true) {
    const [ready, setReady] = useState(false);
    const [widgets, setWidgets] = useState<TossPaymentsWidgets | null>(null);
    const paymentMethodWidgetRef = useRef<WidgetPaymentMethodWidget | null>(null);
    const agreementWidgetRef = useRef<WidgetAgreementWidget | null>(null);

    useEffect(() => {
        loadTossPayments(clientKey)
            .then(tp => setWidgets(tp.widgets({ customerKey })));
    }, []);

    useEffect(() => {
        if (!widgets || !enabled) return;
        const w = widgets;

        async function render() {
            try {
                await w.setAmount(amount);
                const [pm, ag] = await Promise.all([
                    w.renderPaymentMethods({ selector: "#payment-method", variantKey: "DEFAULT" }),
                    w.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
                ]);
                paymentMethodWidgetRef.current = pm;
                agreementWidgetRef.current = ag;
                setReady(true);
            } catch (e: unknown) {
                const code = (e as { code?: string })?.code;
                if (code === "PAYMENT_METHODS_WIDGET_ALREADY_RENDERED" || code === "AGREEMENT_WIDGET_ALREADY_RENDERED") return;
                throw e;
            }
        }
        render();

        return () => {
            paymentMethodWidgetRef.current?.destroy();
            agreementWidgetRef.current?.destroy();
        };
    }, [widgets, enabled]);

    useEffect(() => {
        if (!widgets) return;
        widgets.setAmount(amount).catch(console.error);
    }, [widgets, amount]);

    async function requestPayment(orderId: string, orderName: string, extra?: { subscriptionNo?: string; amount?: number }) {
        if (!widgets) return;
        const base = window.location.origin;
        const failParams = extra?.subscriptionNo
            ? `?subscriptionNo=${extra.subscriptionNo}&amount=${extra.amount ?? 0}`
            : '';
        await widgets.requestPayment({
            orderId,
            orderName,
            successUrl: `${base}/payments/success`,
            failUrl: `${base}/payments/fail${failParams}`,
        });
    }

    return { ready, requestPayment };
}
