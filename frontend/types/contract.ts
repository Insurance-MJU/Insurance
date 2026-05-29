// ─── 계약 목록 행 ────────────────────────────────────────────────────

export interface ContractRow {
  id: string;
  proposalId: string;
  policyNo: string;
  insuredName: string;
  productName: string;
  premium: number;
  appliedAt: string;
  status: string;
}

// ─── 상태 메타 ───────────────────────────────────────────────────────

export const CONTRACT_STATUS_META: Record<string, { label: string; color: string }> = {
  PENDING_REVIEW:       { label: '심사중',     color: 'bg-yellow-100 text-yellow-700' },
  APPROVED:             { label: '승인',       color: 'bg-green-100 text-green-700'   },
  REJECTED:             { label: '거절',       color: 'bg-red-100 text-red-600'       },
  SUPPLEMENT_REQUIRED:  { label: '보완요청',   color: 'bg-orange-100 text-orange-600' },
  // 호환
  PENDING:   { label: '심사중',  color: 'bg-yellow-100 text-yellow-700' },
  ACTIVE:    { label: '유지',    color: 'bg-green-100 text-green-700'   },
  LAPSED:    { label: '실효',    color: 'bg-red-100 text-red-600'       },
  CANCELLED: { label: '거절/해지', color: 'bg-gray-100 text-gray-600'  },
  MATURED:   { label: '만기',    color: 'bg-blue-100 text-blue-600'     },
};
