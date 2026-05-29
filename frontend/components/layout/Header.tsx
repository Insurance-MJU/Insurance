'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';
import { clearSession } from '@/queries/auth';

export default function Header() {
  const pathname = usePathname();
  const { isLoggedIn, role, userId, _hydrated, logout } = useAuthStore();

  // 직원/관리자 경로에서는 헤더 불필요
  if (pathname.startsWith('/employee')) return null;

  // 하이드레이션 전 플래시 방지: 아직 스토어 상태 불명확하면 빈 자리 표시
  if (!_hydrated) {
    return (
      <header className="w-full bg-white/90 border-b sticky top-0 z-50 shadow-sm h-16" />
    );
  }

  // 직원/관리자 역할이면 헤더 숨김
  if (role === 'EMPLOYEE' || role === 'ADMIN') return null;

  const handleLogout = () => {
    clearSession();
    logout();
    window.location.href = '/';
  };

  return (
    <header className="w-full bg-white/90 backdrop-blur-md border-b sticky top-0 z-50 shadow-sm">
      <div className="max-w-5xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link href="/" className="flex items-center gap-2">
          <span className="text-2xl drop-shadow-sm">🛡️</span>
          <span className="font-extrabold text-xl text-slate-800 tracking-tight">한국생명보험</span>
        </Link>
        <nav className="hidden md:flex gap-8 text-[15px] font-bold text-slate-600">
          <Link href="/insurance/products" className="hover:text-blue-600 transition-colors">상품조회</Link>
          <Link href="/insurance/contracts" className="hover:text-blue-600 transition-colors">계약관리</Link>
          <Link href="/insurance/claims?type=accident" className="hover:text-blue-600 transition-colors">보상/청구</Link>
        </nav>
        <div className="flex items-center gap-4">
          {isLoggedIn ? (
            <div className="flex items-center gap-3">
              {userId && <span className="text-sm font-medium text-slate-600">{userId}</span>}
              <button onClick={handleLogout} className="text-sm font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 px-4 py-2 rounded-xl transition-colors">
                로그아웃
              </button>
            </div>
          ) : (
            <>
              <Link href="/auth/login" className="text-sm font-bold text-slate-600 hover:text-blue-600 px-2 py-2">로그인</Link>
              <Link href="/auth/signup" className="text-sm font-bold text-white bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded-xl transition-colors shadow-sm shadow-blue-500/20">회원가입</Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
