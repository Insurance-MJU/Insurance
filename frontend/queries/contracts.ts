import { fetchApi } from '@/queries/api';
import type { ContractRow } from '@/types/contract';

function mapSubscription(s: any): ContractRow {
  return {
    id:          s.subscriptionNo ?? s.id,
    proposalId:  s.subscriptionNo,
    policyNo:    s.contractId ?? s.subscriptionNo,
    insuredName: s.applicantName ?? s.insuredName ?? '',
    productName: s.productName ?? '',
    premium:     s.premium ?? 0,
    appliedAt:   s.subscriptionDate ?? s.appliedAt ?? '',
    status:      s.status ?? '',
  };
}

/** 고객: 내 청약 목록 (JWT 기반 필터링) */
export async function fetchMyContracts(): Promise<ContractRow[]> {
  const res = await fetchApi('/subscriptions', { method: 'GET' });
  const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
  return list.map(mapSubscription);
}

/** 직원: 전체 청약 목록 */
export async function fetchAllContracts(): Promise<ContractRow[]> {
  const res = await fetchApi('/subscriptions', { method: 'GET' });
  const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
  return list.map(mapSubscription);
}

/** 직원: 승인 대기 청약 목록 */
export async function fetchPendingContracts(): Promise<ContractRow[]> {
  const res = await fetchApi('/subscriptions/pending', { method: 'GET' });
  const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
  return list.map(mapSubscription);
}

export async function fetchContract(no: string) {
  return fetchApi(`/subscriptions/${no}`);
}

export async function createContract(data: any) {
  return fetchApi('/subscriptions', { method: 'POST', body: JSON.stringify(data) });
}

export async function approveContract(no: string) {
  return fetchApi(`/subscriptions/${no}/approve`, { method: 'PUT' });
}

export async function rejectContract(no: string, reason: string) {
  return fetchApi(`/subscriptions/${no}/reject`, { method: 'PUT', body: JSON.stringify({ reason }) });
}

export async function supplementContract(no: string, reason: string) {
  return fetchApi(`/subscriptions/${no}/supplement`, { method: 'PUT', body: JSON.stringify({ reason }) });
}
