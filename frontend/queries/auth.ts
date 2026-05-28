import { fetchApi } from './api';

export async function login(data: { userId: string; password: string }) {
    return fetchApi('/auth/login', {
        method: 'POST',
        body: JSON.stringify(data),
    });
}
