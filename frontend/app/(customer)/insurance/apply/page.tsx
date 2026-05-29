'use client';
import { useSearchParams } from 'next/navigation';
import { Suspense, useEffect } from 'react';
import { useEnrollmentStore } from '@/store/enrollmentStore';
import EnrollmentWizard from './_components/EnrollmentWizard';

function ApplyContent() {
  const searchParams    = useSearchParams();
  const prodIdStr       = searchParams.get('prodId') ?? '';
  const setProductId    = useEnrollmentStore((s) => s.setProductId);
  const setProductIdStr = useEnrollmentStore((s) => s.setProductIdStr);

  useEffect(() => {
    if (prodIdStr) {
      setProductIdStr(prodIdStr);
      const num = Number(prodIdStr);
      if (num > 0) setProductId(num);
    }
  }, [prodIdStr]);

  return <EnrollmentWizard />;
}

export default function ApplyPage() {
  return (
    <Suspense fallback={<div className="p-4 text-center text-gray-400">로딩 중...</div>}>
      <ApplyContent />
    </Suspense>
  );
}
