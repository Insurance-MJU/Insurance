import { fetchApi } from './api';
import type { Subscription } from '@/types';

export async function getSubscriptions(): Promise<Subscription[]> {
    return fetchApi('/subscriptions');
}

export async function getPendingSubscriptions(): Promise<Subscription[]> {
    return fetchApi('/subscriptions/pending');
}

export async function getSubscription(no: string): Promise<Subscription> {
    return fetchApi(`/subscriptions/${no}`);
}

export async function createSubscription(data: {
    verificationToken: string;
    productId: string;
    address: string;
    carNumber: string;
    chassisNumber: string;
    premium: number;
    occupation: string;
    carPurpose?: string;
    driverScope?: string;
}) {
    return fetchApi('/subscriptions', {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

export async function approveSubscription(no: string) {
    return fetchApi(`/subscriptions/${no}/approve`, { method: 'PUT' });
}

export async function rejectSubscription(no: string, reason: string) {
    return fetchApi(`/subscriptions/${no}/reject`, {
        method: 'PUT',
        body: JSON.stringify({ reason }),
    });
}

export async function analyzeRisk(no: string) {
    return fetchApi(`/subscriptions/${no}/risk-analysis`, { method: 'POST' });
}

export async function supplementSubscription(no: string, reason: string) {
    return fetchApi(`/subscriptions/${no}/supplement`, {
        method: 'PUT',
        body: JSON.stringify({ reason }),
    });
}
