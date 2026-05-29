'use client';
import Link from 'next/link';

export default function Home() {
    return (
        <main className="min-h-[100dvh] flex flex-col font-sans">
            {/* Hero Section */}
            <section className="relative w-full bg-gradient-to-br from-indigo-50 via-white to-blue-50 py-20 overflow-hidden border-b border-gray-100">
                <div className="absolute top-[-20%] left-[-10%] w-96 h-96 bg-blue-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob" />
                <div className="absolute top-[-10%] right-[-5%] w-96 h-96 bg-indigo-300 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-blob animation-delay-2000" />

                <div className="max-w-6xl mx-auto px-4 flex flex-col lg:flex-row items-center justify-between gap-12 relative z-10">
                    {/* 카피 */}
                    <div className="flex-1 text-center lg:text-left">
                        <div className="inline-block px-4 py-2 bg-blue-100 text-blue-700 font-bold rounded-full text-sm mb-6 shadow-sm">
                            🎉 2026년 신규 상품 출시
                        </div>
                        <h1 className="text-5xl lg:text-6xl font-extrabold text-slate-900 tracking-tight leading-tight mb-6">
                            당신의 내일,<br />
                            <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-500">한국생명보험이</span><br />
                            책임집니다.
                        </h1>
                        <p className="text-lg lg:text-xl text-slate-600 mb-10 leading-relaxed font-medium">
                            복잡한 보험가입은 이제 그만.<br className="hidden lg:block" />
                            스마트 다이렉트 솔루션으로 1분 만에 끝내세요.
                        </p>
                        <div className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
                            <Link
                                href="/insurance/products"
                                className="px-8 py-4 bg-slate-900 hover:bg-slate-800 text-white font-bold rounded-xl text-lg transition-transform active:scale-95 shadow-xl"
                            >
                                내게 맞는 상품 찾기
                            </Link>
                            <Link
                                href="/insurance/calculator"
                                className="px-8 py-4 bg-white hover:bg-gray-50 text-slate-800 border border-gray-200 font-bold rounded-xl text-lg transition-transform active:scale-95 shadow-sm"
                            >
                                1분 보험료 계산
                            </Link>
                        </div>
                    </div>

                    {/* 바로가기 카드 */}
                    <div className="w-full max-w-md lg:w-1/2">
                        <div className="bg-white/90 backdrop-blur-3xl rounded-[2rem] shadow-2xl shadow-blue-900/10 p-8 border border-white/60">
                            <h2 className="text-xl font-bold text-gray-900 mb-6">빠른 서비스</h2>
                            <div className="flex flex-col gap-3">
                                <QuickLink href="/insurance/products" icon="🚗" label="자동차보험 가입" desc="지금 바로 가입하고 할인혜택 받기" />
                                <QuickLink href="/insurance/contracts" icon="📋" label="내 계약 확인" desc="보험 계약 현황 및 상세 정보" />
                                <QuickLink href="/insurance/claims" icon="🏥" label="사고 접수·보험금 청구" desc="빠른 접수로 신속한 보상" />
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            {/* Features Section */}
            <section className="bg-white py-24">
                <div className="max-w-6xl mx-auto px-4">
                    <div className="text-center mb-16">
                        <h2 className="text-3xl font-extrabold text-slate-900 mb-4">왜 한국생명보험인가요?</h2>
                        <p className="text-lg text-slate-500 font-medium">가입부터 보상까지, 압도적으로 편리한 다이렉트 경험</p>
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
                        {[
                            { title: '1분 다이렉트 가입', desc: '복잡한 서류 없이 모바일로 간편하게 가입하세요.', icon: '⚡' },
                            { title: '맞춤형 보험료 계산', desc: '내 차량과 운전 습관으로 가장 합리적인 옵션을 추천받으세요.', icon: '🎯' },
                            { title: '24시간 사고 접수', desc: '언제 어디서든 즉시 모바일로 접수하고 빠르게 보상받으세요.', icon: '🏥' },
                        ].map((f, i) => (
                            <div key={i} className="text-center p-8 rounded-3xl bg-slate-50 hover:bg-blue-50 transition-colors cursor-pointer border border-transparent hover:border-blue-100">
                                <div className="text-4xl mb-6">{f.icon}</div>
                                <h3 className="text-xl font-bold text-slate-900 mb-3">{f.title}</h3>
                                <p className="text-slate-600 font-medium leading-relaxed">{f.desc}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            <style>{`
                @keyframes blob { 0%,100%{transform:translate(0,0) scale(1)}33%{transform:translate(30px,-50px) scale(1.1)}66%{transform:translate(-20px,20px) scale(0.9)} }
                .animate-blob { animation: blob 7s infinite; }
                .animation-delay-2000 { animation-delay: 2s; }
            `}</style>
        </main>
    );
}

function QuickLink({ href, icon, label, desc }: { href: string; icon: string; label: string; desc: string }) {
    return (
        <Link href={href} className="flex items-center gap-4 p-4 rounded-xl border border-gray-100 hover:border-blue-200 hover:bg-blue-50 transition-colors group">
            <div className="text-2xl w-10 text-center">{icon}</div>
            <div className="flex-1">
                <p className="font-semibold text-sm text-gray-900 group-hover:text-blue-700">{label}</p>
                <p className="text-xs text-gray-400 mt-0.5">{desc}</p>
            </div>
            <span className="text-gray-300 group-hover:text-blue-400">›</span>
        </Link>
    );
}
