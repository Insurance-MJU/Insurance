import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function proxy(request: NextRequest) {
    const isLoggedIn = request.cookies.get('is_logged_in')?.value === 'true';
    const userRole = request.cookies.get('user_role')?.value;
    const path = request.nextUrl.pathname;
    const isEmployee = userRole === 'EMPLOYEE' || userRole === 'ADMIN';

    // 직원/관리자 전용 경로 보호
    if (path.startsWith('/employee')) {
        if (!isLoggedIn) {
            const url = new URL('/auth/login', request.url);
            url.searchParams.set('from', path);
            return NextResponse.redirect(url);
        }
        if (!isEmployee) {
            return NextResponse.redirect(new URL('/', request.url));
        }
        if (path === '/employee') {
            return NextResponse.redirect(new URL('/employee/dashboard', request.url));
        }
    }

    // 고객 경로: 직원/관리자는 직원 대시보드로 리다이렉트
    const isCustomerPath = path === '/' || path.startsWith('/insurance');
    if (isCustomerPath && isLoggedIn && isEmployee) {
        return NextResponse.redirect(new URL('/employee/dashboard', request.url));
    }

    // 고객 보호 경로 (비로그인 차단)
    if (path.startsWith('/insurance/contracts') || path.startsWith('/insurance/claims')) {
        if (!isLoggedIn) {
            const url = new URL('/auth/login', request.url);
            url.searchParams.set('from', path);
            return NextResponse.redirect(url);
        }
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/', '/employee/:path*', '/insurance/:path*'],
};
