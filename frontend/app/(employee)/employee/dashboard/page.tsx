import Link from 'next/link';

const CARDS = [
    { href: '/employee/subscriptions', icon: '📋', title: '청약 심사', desc: '신규 가입 신청 검토 및 승인/반려' },
    { href: '/employee/claims', icon: '💰', title: '보험금 처리', desc: '손해사정 및 보험금 지급 처리' },
];

export default function DashboardPage() {
    return (
        <div className="max-w-4xl mx-auto px-6 py-12">
            <h1 className="text-3xl font-extrabold text-slate-900 mb-2">대시보드</h1>
            <p className="text-slate-500 mb-10">업무를 선택하세요</p>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {CARDS.map(card => (
                    <Link
                        key={card.href}
                        href={card.href}
                        className="bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md hover:border-blue-100 transition-all p-8 flex items-center gap-6"
                    >
                        <div className="text-4xl">{card.icon}</div>
                        <div>
                            <h2 className="text-xl font-bold text-slate-900">{card.title}</h2>
                            <p className="text-sm text-slate-500 mt-1">{card.desc}</p>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}
