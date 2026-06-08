import { DriverScope, FamilyMember } from '@/types/enrollment';

export const VERIFICATION_METHODS = [
  { value: 'PHONE', label: '문자(SMS)' },
  { value: 'KAKAO', label: '카카오' },
  { value: 'TOSS',  label: '토스' },
  { value: 'PASS',  label: 'PASS' },
] as const;

export const DRIVER_SCOPES: { value: DriverScope; label: string; desc: string }[] = [
  { value: 'NAMED_ONLY', label: '본인 한정',  desc: '기명피보험자(본인)만 운전' },
  { value: 'COUPLE',     label: '부부 한정',  desc: '기명피보험자 및 배우자' },
  { value: 'FAMILY',     label: '가족 한정',  desc: '기명피보험자 및 배우자·부모·자녀' },
  { value: 'ALL',        label: '누구나',     desc: '운전자 제한 없음' },
];

export const FAMILY_RELATIONS: { value: FamilyMember['relation']; label: string }[] = [
  { value: 'SPOUSE', label: '배우자' },
  { value: 'PARENT', label: '부모' },
  { value: 'CHILD',  label: '자녀' },
  { value: 'OTHER',  label: '기타' },
];

export const REQUIRED_CONSENT_ITEMS = [
  { key: 'personal',   label: '[필수] 개인정보 수집·이용 동의' },
  { key: 'thirdParty', label: '[필수] 개인정보 제3자 제공 동의' },
  { key: 'credit',     label: '[필수] 개인신용정보 조회·이용 동의' },
  { key: 'marketing',  label: '[필수] 보험계약 관련 마케팅 수신 동의' },
];

export const VEHICLE_PURPOSES = [
  { value: 'COMMUTE',  label: '출퇴근/가정용', desc: '직장 통학 및 일상 가정 생활 목적' },
  { value: 'BUSINESS', label: '업무용',       desc: '사업 목적의 운행 포함' },
  { value: 'DELIVERY', label: '영업용',       desc: '배달·운수업 등 유상 운송' },
] as const;
