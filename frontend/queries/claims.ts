import { fetchApi } from './api';
import type { Claim, Accident, Investigator } from '@/types';

export async function getAccidents(date?: string, status?: string): Promise<Accident[]> {
    const params = new URLSearchParams();
    if (date) params.set('date', date);
    if (status) params.set('status', status);
    const query = params.toString() ? `?${params}` : '';
    return fetchApi(`/accidents${query}`);
}

export async function reportAccident(data: {
    contractId: string;
    reportedBy: string;
    phone: string;
    accidentDate: string;
    accidentLocation: string;
    accidentDetail: string;
    documents: string;
}) {
    return fetchApi('/accidents', {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

export async function getInvestigation(accidentId: string) {
    return fetchApi(`/accidents/${accidentId}/investigation`);
}

export async function investigateDamage(accidentId: string, data: {
    opinion: string;
    damageCode: string;
    injuryGrade: number;
    ourFault: number;
    otherFault: number;
    liability: string;
    finalOpinion: string;
}) {
    return fetchApi(`/accidents/${accidentId}/investigation`, {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

export async function getInvestigators(specialty?: string): Promise<Investigator[]> {
    const query = specialty ? `?specialty=${encodeURIComponent(specialty)}` : '';
    return fetchApi(`/investigators${query}`);
}

export async function assignAccident(accidentId: string, employeeId: string) {
    return fetchApi(`/accidents/${accidentId}/assign`, {
        method: 'PUT',
        body: JSON.stringify({ employeeId }),
    });
}

export async function getClaims(): Promise<Claim[]> {
    return fetchApi('/claims');
}

export async function assessClaim(id: string, data: { settlement: number; deductible: number }) {
    return fetchApi(`/claims/${id}/assess`, {
        method: 'PUT',
        body: JSON.stringify(data),
    });
}

export async function payClaim(id: string, data: { bank: string; accountNo: string }) {
    return fetchApi(`/claims/${id}/pay`, {
        method: 'PUT',
        body: JSON.stringify(data),
    });
}
