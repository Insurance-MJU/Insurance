import { fetchApi } from './api';
import type { Product, PremiumEstimateResponse, Coverage, Rider } from '@/types';

export async function getProducts(onSale?: boolean): Promise<Product[]> {
    const query = onSale ? '?onSale=true' : '';
    return fetchApi(`/products${query}`);
}

export async function getProduct(id: string): Promise<Product> {
    return fetchApi(`/products/${id}`);
}

export async function estimatePremium(
    id: string,
    data: { carStandardValue: number; carPurpose: string }
): Promise<PremiumEstimateResponse> {
    return fetchApi(`/products/${id}/estimate`, {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

export async function getCoverages(): Promise<Coverage[]> {
    return fetchApi('/coverages');
}

export async function getRiders(): Promise<Rider[]> {
    return fetchApi('/riders');
}

export async function calculatePremium(id: string, data: {
    targetSales: number;
    lossRatio: number;
    salesExpense: number;
    adminExpense: number;
}) {
    return fetchApi(`/products/${id}/premium-calculation`, {
        method: 'POST',
        body: JSON.stringify(data),
    });
}

export async function applyForApproval(id: string): Promise<Product> {
    return fetchApi(`/products/${id}/approval`, { method: 'PUT' });
}

export async function applyRateVerification(id: string): Promise<Product> {
    return fetchApi(`/products/${id}/rate-verification`, { method: 'PUT' });
}

export async function confirmSale(id: string): Promise<Product> {
    return fetchApi(`/products/${id}/sale`, { method: 'PUT' });
}
