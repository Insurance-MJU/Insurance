import Link from "next/link";

const sections = [
    { label: "기초율",       href: "/employee/master/base-rate",  desc: "차종·연령·성별 등 기초 요율 데이터 관리" },
    { label: "담보",         href: "/employee/master/coverage",    desc: "담보 종류·보장 내용·약관 조항 관리" },
    { label: "특약",         href: "/employee/master/rider",       desc: "선택 특약 항목 관리" },
    { label: "공통 면책사유", href: "/employee/master/exclusion",  desc: "보험금 부지급 공통 면책사유 관리" },
    { label: "표준약관조항",  href: "/employee/master/provisions", desc: "금감원 표준 약관 조항 원문 관리" },
];

export default function MasterPage() {
    return (
        <div className="max-w-3xl">
            <h1 className="text-xl font-bold text-gray-800 mb-1">기준정보관리</h1>
            <p className="text-sm text-gray-500 mb-6">상품 설계에 사용되는 기준 데이터를 관리합니다.</p>
            <div className="grid grid-cols-2 gap-4">
                {sections.map(s => (
                    <Link key={s.href} href={s.href}
                        className="block p-5 bg-white border border-gray-200 rounded-xl hover:border-blue-300 hover:shadow-sm transition-all">
                        <p className="font-semibold text-gray-800 mb-1">{s.label}</p>
                        <p className="text-xs text-gray-400">{s.desc}</p>
                    </Link>
                ))}
            </div>
        </div>
    );
}
