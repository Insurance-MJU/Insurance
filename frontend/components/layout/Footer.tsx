'use client';
import { useEffect, useState } from 'react';
import { usePathname } from 'next/navigation';

export default function Footer() {
  const pathname = usePathname();
  const [isEmployee, setIsEmployee] = useState(false);
  useEffect(() => {
    const match = document.cookie.match(/(?:^|; )user_role=([^;]*)/);
    const role = match ? decodeURIComponent(match[1]) : null;
    setIsEmployee(role === 'EMPLOYEE' || role === 'ADMIN');
  }, []);

  if (pathname.startsWith('/employee') || isEmployee) return null;

  return (
    <footer className="w-full bg-slate-50 border-t mt-auto">
      <div className="max-w-5xl mx-auto px-4 py-10">
        <div className="flex flex-col md:flex-row justify-between items-start gap-6 mb-8 text-sm text-slate-500">
          <div>
            <h3 className="font-extrabold text-slate-800 text-lg mb-3 flex items-center gap-2">
              <span className="text-lg">🛡️</span> 한국생명보험(주)
            </h3>
            <p className="leading-relaxed">
              대표이사: 홍길동 | 사업자등록번호: 123-45-67890<br/>
              서울특별시 강남구 테헤란로 123, 한국생명보험 타워
            </p>
          </div>
          <div className="flex gap-12">
            <div className="flex flex-col gap-3">
              <span className="font-bold text-slate-800 mb-1">고객센터</span>
              <span className="font-semibold text-blue-600 text-base">1588-0000 <span className="text-slate-500 text-sm font-normal">(평일 09:00 - 18:00)</span></span>
              <span>사고접수 1588-1111 <span className="text-slate-500 text-sm font-normal">(24시간)</span></span>
            </div>
            <div className="flex flex-col gap-3">
              <span className="font-bold text-slate-800 mb-1">빠른 링크</span>
              <a href="#" className="hover:text-blue-600 transition-colors">이용약관</a>
              <a href="#" className="hover:text-blue-600 transition-colors font-bold text-slate-700">개인정보처리방침</a>
            </div>
          </div>
        </div>
        <p className="text-center text-xs text-slate-400 font-medium">
          © 2026 Korea Life Insurance Co., Ltd. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
