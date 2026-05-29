const SERVER_URL = process.env.NEXT_PUBLIC_JAVA_SERVER_URL;

export async function confirmPayment(data: { paymentKey: string; orderId: string; amount: string }) {
    const response = await fetch(`${SERVER_URL}/payments/confirm`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!response.ok) throw await response.json();
    return response.json();
}
export async function cancelPayment(data: { paymentKey: string; orderId: string; amount: string }) {
    const response = await fetch(`${SERVER_URL}/payments/${data.paymentKey}/cancel`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    })
    if (!response.ok) throw await response.json();
    return response.json();
}

export async function getPaymentDetails(paymentKey: string) {
    const response = await fetch(`${SERVER_URL}/payments/${paymentKey}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
    })
    if (!response.ok) throw await response.json();
    return response.json();
}
