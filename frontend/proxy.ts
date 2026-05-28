import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function proxy(request: NextRequest) {
    const isLoggedIn = request.cookies.get('is_logged_in')?.value === 'true';
    const userRole = request.cookies.get('user_role')?.value;
    const path = request.nextUrl.pathname;
    const isEmployee = userRole === 'EMPLOYEE' || userRole === 'ADMIN';

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
