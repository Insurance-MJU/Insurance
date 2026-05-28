const BASE_URL = process.env.NEXT_PUBLIC_JAVA_SERVER_URL;

export class ApiError extends Error {
    public status?: number;
    constructor(message: string, status?: number) {
        super(message);
        this.status = status;
    }
}

export function getCookie(name: string): string | null {
    if (typeof document === 'undefined') return null;
    const match = document.cookie.match(new RegExp(`(?:^|; )${name}=([^;]*)`));
    return match ? decodeURIComponent(match[1]) : null;
}

function setCookie(name: string, value: string, maxAge: number) {
    document.cookie = `${name}=${encodeURIComponent(value)}; path=/; max-age=${maxAge}`;
}

function clearSession() {
    ['is_logged_in', 'user_role', 'access_token'].forEach(
        k => (document.cookie = `${k}=; path=/; max-age=0`)
    );
    window.location.href = '/auth/login';
}

async function request(path: string, options: RequestInit): Promise<any> {
    const token = getCookie('access_token');
    const headers: Record<string, string> = { ...(options.headers as Record<string, string>) };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    if (!(options.body instanceof FormData)) {
        headers['Content-Type'] = headers['Content-Type'] || 'application/json';
    }

    const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });

    if (res.status === 401) {
        clearSession();
        throw new ApiError('로그인이 필요합니다.', 401);
    }
    if (!res.ok) {
        let errorData: any = {};
        try { errorData = await res.json(); } catch {}
        throw new ApiError(errorData.message || '요청에 실패했습니다.', res.status);
    }
    if (res.status === 204) return null;
    const json = await res.json();
    // 백엔드가 List 응답을 { data: [...] } 로 래핑함
    return Array.isArray(json?.data) ? json.data : json;
}

export async function fetchApi(path: string, options: RequestInit = {}) {
    return request(path, options);
}

export function saveSession(data: { accessToken: string; role: string; name: string }) {
    const maxAge = 86400;
    setCookie('is_logged_in', 'true', maxAge);
    setCookie('user_role', data.role, maxAge);
    setCookie('user_name', data.name, maxAge);
    setCookie('access_token', data.accessToken, maxAge);
}
