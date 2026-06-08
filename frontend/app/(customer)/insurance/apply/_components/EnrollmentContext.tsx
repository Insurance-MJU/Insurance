'use client';
/**
 * EnrollmentContext는 zustand 스토어의 래퍼입니다.
 * 하위 Step 컴포넌트는 이 파일 대신 @/store/enrollmentStore를 직접 import합니다.
 * EnrollmentProvider는 이전 호환성을 위해 유지합니다.
 */
export { useEnrollment, useEnrollmentStore } from '@/store/enrollmentStore';
export type { EnrollmentState } from '@/types/enrollment';

import { ReactNode } from 'react';

/** zustand는 Provider가 필요 없지만 기존 구조 호환을 위해 유지 */
export function EnrollmentProvider({ children }: { children: ReactNode }) {
  return <>{children}</>;
}
