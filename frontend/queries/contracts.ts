import { fetchApi } from '@/queries/api';
import type { ContractRow } from '@/types/contract';

function mapSubscription(s: any): ContractRow {
  return {
    id:             s.subscriptionNo ?? s.id,
    proposalId:     s.subscriptionNo,
    policyNo:       s.contractId ?? '',
    insuredName:    s.applicantName ?? s.insuredName ?? '',
    productName:    s.productName ?? '',
    premium:        s.premium ?? 0,
    appliedAt:      s.subscriptionDate ?? s.appliedAt ?? '',
    status:         s.status ?? '',
    contractStatus: s.contractStatus ?? undefined,
  };
}

function mapContract(c: any): ContractRow {
  return {
    id:          c.contractId ?? c.id,
    proposalId:  c.subscriptionNo ?? '',
    policyNo:    c.policyNo ?? '',
    insuredName: c.holderName ?? '',
    productName: c.productName ?? '',
    premium:     c.premium ?? 0,
    appliedAt:   c.issueDate ?? '',
    status:      c.status ?? '',
  };
}

/** 고객: 내 청약 현황 (전체 상태) */
export async function fetchMySubscriptions(): Promise<ContractRow[]> {
  const res = await fetchApi('/subscriptions', { method: 'GET' });
  const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
  return list.map(mapSubscription);
}

/** 고객: 내 계약 목록 */
export async function fetchMyContracts(): Promise<ContractRow[]> {
  const res = await fetchApi('/contracts', { method: 'GET' });
  const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
  return list.map(mapContract);
}

/** 직원: 전체 계약 목록 */
export async function fetchAllContracts(): Promise<ContractRow[]> {
  const res = await fetchApi('/contracts', { method: 'GET' });
  const list = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
  return list.map(mapContract);
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
