import { fetchApi } from "./api";

const SESSION_COOKIE_KEYS = ["is_logged_in", "user_role", "access_token"] as const;

export function saveSession(res: { role: string; accessToken: string; name?: string; refreshToken?: string }) {
    const maxAge = 86400;
    document.cookie = `is_logged_in=true; path=/; max-age=${maxAge}`;
    document.cookie = `user_role=${res.role}; path=/; max-age=${maxAge}`;
    document.cookie = `access_token=${encodeURIComponent(res.accessToken)}; path=/; max-age=${maxAge}`;
    if (res.name) document.cookie = `user_name=${encodeURIComponent(res.name)}; path=/; max-age=${maxAge}`;
}

export function saveIdentityToken(token: string) {
    document.cookie = `identity_verify_token=${encodeURIComponent(token)}; path=/; max-age=86400`;
}

export function clearSession() {
    SESSION_COOKIE_KEYS.forEach((k) => {
        document.cookie = `${k}=; path=/; max-age=0`;
    });
}

export async function login(data: any) {
    return fetchApi("/auth/login", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

// Insurance 백엔드에 미구현 - 추후 확장 필요
export async function signup(data: any) {
    return fetchApi("/auth/signup", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function loginByIdentity(verifyToken: string) {
    return fetchApi("/auth/login/identity", {
        method: "POST",
        body: JSON.stringify({ verifyToken }),
    });
}

export async function sendOtp(data: any) {
    return fetchApi("/verification/send-otp", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

export async function verifyOtp(data: any) {
    return fetchApi("/verification/verify-otp", {
        method: "POST",
        body: JSON.stringify(data),
    });
}
