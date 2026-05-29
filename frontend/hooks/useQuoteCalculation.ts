import { useState, useCallback } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import { fetchApi } from '@/queries/api';

// vehiclePurpose → 백엔드 CarPurpose enum 매핑
const PURPOSE_MAP: Record<string, string> = {
  COMMUTE:    'COMMUTE',
  BUSINESS:   'BUSINESS',
  COMMERCIAL: 'BUSINESS',
  HOME:       'COMMUTE',
};

export function useQuoteCalculation() {
  const {
    productId, productIdStr, vehicleInfo, carNumber, vehiclePurpose,
    driverScope, mileageDiscount, selectedAdjustments,
    hasBlackbox, hasAdvancedSafety, overrideValue,
    quote, setQuote,
  } = useEnrollmentStore();

  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const fetchQuote = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const carStandardValue = overrideValue ?? vehicleInfo?.standardValue ?? 30_000_000;
      const carPurpose = PURPOSE_MAP[vehiclePurpose] ?? 'COMMUTE';
      // productIdStr: "PROD-001" 형식, productId: 숫자 (없으면 fallback)
      const pid = productIdStr || (productId > 0 ? String(productId) : 'PROD-001');

      const res = await fetchApi(`/products/${pid}/estimate`, {
        method: 'POST',
        body: JSON.stringify({ productId: String(pid), carStandardValue, carPurpose, driverAge: 25 }),
      });

      const data = res.data ?? res;
      const basePremium: number = data.subtotal ?? data.finalPremium ?? 1_150_000;
      const finalPremium: number = data.finalPremium ?? basePremium;

      // 할인 계산
      const discounts: { name: string; amount: number }[] = [];
      if (mileageDiscount) discounts.push({ name: '마일리지 할인', amount: Math.round(basePremium * 0.047) });
      if (hasBlackbox)     discounts.push({ name: '블랙박스 할인', amount: Math.round(basePremium * 0.03) });
      if (hasAdvancedSafety) discounts.push({ name: '안전장치 할인', amount: Math.round(basePremium * 0.02) });
      selectedAdjustments.forEach(a => {
        if (a.rate) discounts.push({ name: a.itemName, amount: Math.round(basePremium * a.rate) });
      });
      const totalDiscount = discounts.reduce((s, d) => s + d.amount, 0);

      // 담보 구성 (예시 배분)
      const coverages = [
        { name: '대인배상 I (의무)', limitDesc: '무한',    premium: Math.round(finalPremium * 0.20) },
        { name: '대인배상 II',      limitDesc: '한도 5억', premium: Math.round(finalPremium * 0.25) },
        { name: '대물배상',         limitDesc: '한도 2억', premium: Math.round(finalPremium * 0.28) },
        { name: '자동차상해',       limitDesc: '1억',      premium: Math.round(finalPremium * 0.08) },
        { name: '무보험차상해',     limitDesc: '2억',      premium: Math.round(finalPremium * 0.05) },
        { name: '자기차량손해',     limitDesc: '차량가액', premium: Math.round(finalPremium * 0.14) },
      ];

      setQuote({
        coverages,
        appliedDiscounts: discounts,
        totalBeforeDiscount: finalPremium,
        totalDiscount,
        totalPremium: finalPremium - totalDiscount,
        riskGrade: '3등급',
        discountSurchargeGrade: '기본',
      });
    } catch {
      setError('보험료 계산 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  }, [productId, productIdStr, vehiclePurpose, overrideValue, vehicleInfo, mileageDiscount,
      hasBlackbox, hasAdvancedSafety, selectedAdjustments, setQuote]);

  const resetQuote = useCallback(() => setQuote(null), [setQuote]);

  return { quote, loading, error, fetchQuote, resetQuote };
}
