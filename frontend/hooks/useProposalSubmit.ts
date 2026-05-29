import { useState } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { fetchApi } from '@/queries/api';

export function useProposalSubmit() {
  const {
    productIdStr, vehicleInfo, carNumber, vehiclePurpose,
    driverScope, insured, contractor, isSamePerson,
    verificationToken, quote, proposalId, policyNo, setProposal,
  } = useEnrollmentStore();

  const [submitting, setSubmitting] = useState(false);
  const [error, setError]           = useState('');

  const handleSubmit = async () => {
    setSubmitting(true);
    setError('');
    try {
      const pid     = productIdStr || 'PROD-001';
      const premium = quote?.totalPremium ?? 0;

      const res = await fetchApi('/subscriptions', {
        method: 'POST',
        body: JSON.stringify({
          verificationToken: verificationToken ?? '',
          productId:    pid,
          address:      '주소 미입력',
          carNumber,
          chassisNumber: '',
          occupation:   '기타',
          carPurpose:   vehiclePurpose ?? 'COMMUTE',
          driverScope:  driverScope ?? 'NAMED_ONLY',
          premium,
        }),
      });

      const data = res.data ?? res;
      setProposal(data.subscriptionNo ?? data.proposalId ?? '');

      // 청약에 사용된 실명으로 user_name 쿠키 갱신 (계약 현황 필터에 사용)
      if (insured?.name) {
        document.cookie = `user_name=${encodeURIComponent(insured.name)}; path=/; max-age=86400`;
      }
    } catch (e: any) {
      setError(e?.message ? `청약 제출 실패: ${e.message}` : '청약 제출에 실패했습니다.');
    } finally {
      setSubmitting(false);
    }
  };

  return {
    handleSubmit, submitting, error, quote, policyNo,
    isSamePerson, insured, contractor, carNumber, vehicleInfo,
    driverScope, proposalId,
    polling: false, status: proposalId ? 'PENDING_REVIEW' : null,
  };
}
