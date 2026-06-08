// ─── 금액 포맷 ────────────────────────────────────────────────────────

/** 1234567 → "1,234,567원" */
export function formatMoney(amount: number): string {
  return amount.toLocaleString('ko-KR') + '원';
}

/** 1234567 → "1,234,567" (단위 없음) */
export function formatMoneyRaw(amount: number): string {
  return amount.toLocaleString('ko-KR');
}

// ─── 날짜 포맷 ────────────────────────────────────────────────────────

/** "2026-04-25" → "2026년 4월 25일" */
export function formatDate(dateStr: string): string {
  if (!dateStr) return '';
  const d = new Date(dateStr);
  return `${d.getFullYear()}년 ${d.getMonth() + 1}월 ${d.getDate()}일`;
}

/** ISO string → "2026.04.25 13:45" */
export function formatDateTime(isoStr: string): string {
  if (!isoStr) return '';
  const d = new Date(isoStr);
  const ymd = `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`;
  const hm = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  return `${ymd} ${hm}`;
}

// ─── 주민번호 / 나이 ──────────────────────────────────────────────────

/**
 * 주민번호 앞 6자리(YYMMDD) + 뒷자리 첫 번째 숫자로 만 나이 계산
 * genderDigit: '1'|'2' → 1900년대, '3'|'4' → 2000년대
 */
export function calcAgeFromSsn(ssnFront: string, genderDigit: string): number {
  if (ssnFront.length < 6) return 0;
  const yy = parseInt(ssnFront.substring(0, 2), 10);
  const mm = parseInt(ssnFront.substring(2, 4), 10) - 1;
  const dd = parseInt(ssnFront.substring(4, 6), 10);
  const gd = parseInt(genderDigit, 10);
  const birthYear = (gd === 3 || gd === 4) ? 2000 + yy : 1900 + yy;
  const today = new Date();
  let age = today.getFullYear() - birthYear;
  if (today.getMonth() < mm || (today.getMonth() === mm && today.getDate() < dd)) age--;
  return age;
}

/**
 * 출생연도(4자리) → 만 나이
 */
export function calcAgeFromBirthYear(birthYear: number): number {
  return new Date().getFullYear() - birthYear;
}

/**
 * 만 나이 → 운전자 최소연령 보험 요율 구간
 * 가장 어린 드라이버의 나이를 넣으면 해당 구간 반환
 */
export function ageToMinAgeBracket(age: number): number {
  if (age < 21) return 0;
  if (age < 26) return 21;
  if (age < 30) return 26;
  if (age < 35) return 30;
  if (age < 43) return 35;
  if (age < 48) return 43;
  return 48;
}

// ─── 주민번호 마스킹 ──────────────────────────────────────────────────

/** "9001011234567" → "900101-1******" */
export function maskSsn(ssn: string): string {
  const digits = ssn.replace(/-/g, '');
  if (digits.length < 7) return ssn;
  return `${digits.slice(0, 6)}-${digits[6]}******`;
}
