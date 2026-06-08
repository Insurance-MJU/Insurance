export interface MenuItem {
  label: string;
  href: string;
  exact?: boolean;
}

export interface MenuGroup {
  label: string;
  href: string;
  children: MenuItem[];
}

export const EMPLOYEE_MENU: MenuGroup[] = [
  {
    label: '홈',
    href: '/employee/dashboard',
    children: [],
  },
  {
    label: '기준정보관리',
    href: '/employee/master',
    children: [
      { label: '기초율',        href: '/employee/master/base-rate' },
      { label: '담보',          href: '/employee/master/coverage' },
      { label: '특약',          href: '/employee/master/rider' },
      { label: '공통 면책사유', href: '/employee/master/exclusion' },
      { label: '표준약관조항',  href: '/employee/master/provisions' },
    ],
  },
  {
    label: '상품관리',
    href: '/employee/products',
    children: [
      { label: '상품목록', href: '/employee/products',          exact: true },
      { label: '요율확인', href: '/employee/products/rate-check' },
      { label: '인가신청', href: '/employee/products/license' },
      { label: '판매신청', href: '/employee/products/sales' },
    ],
  },
  {
    label: '계약관리',
    href: '/employee/contracts',
    children: [
      { label: '계약 목록', href: '/employee/contracts',        exact: true },
      { label: '청약 심사', href: '/employee/contracts/pending' },
      { label: '계약 조회', href: '/employee/contracts/search' },
    ],
  },
  {
    label: '보상관리',
    href: '/employee/accidents',
    children: [
      { label: '사고 접수',   href: '/employee/accidents', exact: true },
      { label: '손해 처리',   href: '/employee/claims',    exact: true },
    ],
  },
];
