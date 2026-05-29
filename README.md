# 보험 관리 시스템

자동차 보험 상품 관리 시스템 (Java 백엔드 + Next.js 프론트엔드)

---

## 실행 방법

### 1. DB 설정

MySQL이 설치되어 있어야 합니다. (기본 포트 3306)

```cmd
mysql -u root -p0000 < resources/schema.sql
```

**기존 DB가 있는 경우** (처음 셋업이 아닌 경우) 아래 마이그레이션 실행:
```cmd
mysql -u root -p0000 insurance_db -e "ALTER TABLE accidents ADD COLUMN IF NOT EXISTS user_id VARCHAR(50) NULL AFTER accident_id; ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS user_id VARCHAR(50) NULL AFTER subscription_no;"
```

```cmd
mysql -u root -p0000 insurance_db -e "UPDATE products SET status='DESIGNING' WHERE status IN ('DESIGN','DESIGN_COMPLETE'); UPDATE products SET status='FSS_APPLIED' WHERE status='APPROVAL_PENDING'; UPDATE products SET status='FSS_APPROVED' WHERE status='APPROVED'; UPDATE products SET status='FILING' WHERE status='SALE_PENDING';"
```

DB 접속 정보는 `resources/application.properties`에서 수정 가능합니다.

---

### 2. 백엔드 실행 (Java)

IntelliJ에서 `Main.java`를 실행하거나, 빌드 후 아래 명령어 사용:

```cmd
java -jar app.jar web
```

기본 포트: **8080**

---

### 3. 프론트엔드 실행 (Next.js)

```cmd
cd frontend
npm install
npm run dev
```

기본 포트: **3000** → `http://localhost:3000`

`.env.local` 파일이 없다면 `frontend/` 디렉토리에 생성:
```
NEXT_PUBLIC_JAVA_SERVER_URL=http://localhost:8080
```

---

## 테스트 계정

| 역할 | 아이디 | 비밀번호 |
|------|--------|---------|
| 고객 | customer1 | 1234 |
| 직원 | employee1 | 1234 |
| 관리자 | admin1 | 1234 |

---

## 주요 기능 및 시나리오

### 상품 관리 (직원)
1. **상품관리 → 신규등록** : 상품 설계 (4단계 위저드)
2. **상품관리 → 요율확인** : 보험개발원 제출 → 수령 완료 처리
3. **상품관리 → 인가신청** : 금감원 인가 신청 → 인가 완료 처리
4. **상품관리 → 판매신청** : 판매 신청 → 판매 확정 → 판매 시작

### 계약 관리 (직원)
- **계약관리 → 청약심사** : 고객 청약 승인/거절/보완 요청

### 보상 관리 (직원)
1. **보상관리 → 사고 접수** : 사고 조회 및 현장조사역 배당
2. **보상관리 → 손해 처리** : 손해 사정 → 보험금 지급

### 고객
1. **상품조회** : 판매 중 상품 목록 및 상세 보기
2. **보험 가입** : 본인인증 → 차량 정보 → 담보/특약 → 보험료 확인 → 청약 제출
3. **계약관리** : 내 계약 현황 조회
4. **보상/청구** : 사고 접수 및 보험금 청구, 사고 접수 내역 조회

---

## 기술 스택

- **백엔드**: Java (Spring 없음), 자체 구현 HTTP 서버, MySQL
- **프론트엔드**: Next.js, TypeScript, Tailwind CSS, Zustand
- **인증**: JWT (jjwt)
- **외부 서비스**: 모두 Mock 구현 (은행, 신용조회, 금감원, 보험개발원, 차량조회, 본인인증)
