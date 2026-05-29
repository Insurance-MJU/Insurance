import PaymentWidget from "@/components/payments/PaymentWidget";

export default function Page() {
    return (
        <main style={{ maxWidth: 560, width: "100%", margin: "0 auto" }}>
            <h1 className="text-xl font-bold mb-6">보험료 결제</h1>
            <PaymentWidget />
        </main>
    );
}
