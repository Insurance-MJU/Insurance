# CT 유즈케이스 하드코딩 현황 검토

> 검토 기준: UI 레이어에 박혀 있는 값 중 도메인·인프라 계층으로 이동해야 하는 것과
> 법령·시나리오 고정값으로 상수가 적절한 것을 구분한다.

---

## CT-01 · 상품을 설계한다 ✅ 해결 완료

| 항목 | 이전 | 이후 |
|------|------|------|
| 담보 목록 (이름·필수여부) | `String[] COVERAGE_NAMES`, `boolean[] COVERAGE_MANDATORY` 상수 | `CoverageRepository.findAll()` |
| 담보 가입옵션 | `String[][] COVERAGE_OPTIONS` 상수 | `Coverage.getLimitOptions()` → `CoverageLimitOption.getOptionName()` |
| 특약 목록 | `String[] RIDER_NAMES` 상수 (레포 미사용) | `RiderRepository.findAll()` |
| A1 필수담보 검사 | 문자열 인덱스(0번) 하드코딩 | `Coverage.getCoverageType() == PERSONAL_INJURY_MANDATORY` |

---

## CT-02 · 보험료를 산출한다 ⚠️ 부분 해결

### 해결된 것
- NPV / BEP / 합산비율 계산 → `domain.ProfitabilityCalculator`로 분리
- IRR → `ProfitabilityCalculator.IRR_ESTIMATE_PCT` 상수 (계리 반복계산 불가 → 추정치 명시)

### 남은 하드코딩

| 위치 | 값 | 성격 | 권장 처리 |
|------|----|------|-----------|
| `runAsInclude()` L96–97 | 참조위험률 78.5%, 68.2% | KIDI 제공 데이터 | `KidiClient.getReferenceLossRates()` 반환으로 이동 |
| `runAsInclude()` L122 | 기본 보험료 `1_150_000L` | 표준 시장가 가정치 | 담보 구성 기반 계산으로 개선 가능 (복잡도 높음) |
| `runAsInclude()` L125 | 준비금 비율 `0.40` | 보험업법 법정 비율(40%) | **상수 유지 적절** — `LEGAL_RESERVE_RATIO` 상수명 부여 권장 |
| `runAsInclude()` L148 | NPV 할인율 `5.0`, 기간 `2년` | 내부 계리 가정 | `ProfitabilityCalculator.npv()` 파라미터로 문서화 |
| `runAsInclude()` L145 | 초기투자비 비율 `0.10` | 내부 가정치 | `ProfitabilityCalculator.bep()` 파라미터로 문서화 |

---

## CT-03 · 기초서류를 등록한다 ⚠️ 검토 필요

```java
private static final String[] DOC_NAMES = {"사업방법서", "보험약관", "산출방법서"};
private static final ProductDocument.DocType[] DOC_TYPES = { BASIC_DOCUMENT, GENERAL_TERMS, BASIC_DOCUMENT };
```

| 판단 | 근거 |
|------|------|
| **상수 유지 가능** | 보험업법 제5조 기초서류 3종은 법정 고정 목록 |
| 개선 여지 | `DocType` enum 값에 표시명을 추가하면 배열 2개를 enum 순회 1개로 줄일 수 있음 |

**권장**: `DocType` enum에 `displayName` 필드 추가

```java
public enum DocType {
    BASIC_DOCUMENT("사업방법서"),
    GENERAL_TERMS("보험약관"),
    // ...
    private final String displayName;
}
```

---

## CT-04 · 상품인가를 신청한다 ✅ 별도 상수 없음

- CT-05 include, FSS 제출, 상태 전이 로직만 존재
- `ProductDocument.DocType.APPROVAL_APPLICATION` 등 enum 직접 사용 → 적절

---

## CT-05 · 요율검증을 요청한다 ⚠️ 검토 필요

```java
private static final String[] REQUIRED_DOCS = {"요율 산출 근거서", "담보별 기준 순보험료 산출표"};
```

| 판단 | 근거 |
|------|------|
| **외부기관 요건** | 보험개발원이 요율검증 시 요구하는 제출서류 목록 |
| 권장 처리 | `KidiClient.getRequiredVerificationDocs()` 로 이동 |

```java
// KidiClient.java에 추가
public List<String> getRequiredVerificationDocs() {
    return Arrays.asList("요율 산출 근거서", "담보별 기준 순보험료 산출표");
}
```

CT-05에서 `kidiClient.getRequiredVerificationDocs()` 호출로 교체하면
실제 KIDI API 연동 시 mock 교체만으로 대응 가능.

---

## CT-06 · 상품판매를 확정한다 ⚠️ 검토 필요

```java
private static final String[] REQUIRED_DOCS = {"상품 신고서", "수익성 분석 보고서", "공시자료"};
private static final ProductDocument.DocType[] DOC_TYPES = { SALE_NOTIFICATION, PROFITABILITY_REPORT, DISCLOSURE };
```

| 판단 | 근거 |
|------|------|
| **외부기관 요건** | 금융감독원 판매 신고 시 필수 제출서류 |
| 권장 처리 | `FssClient.getRequiredSaleDocs()` 로 이동 |

CT-05와 동일한 패턴. `DocType[]`은 `DocType` enum의 `displayName` 추가 후 제거 가능.

---

## 전체 우선순위 요약

| 우선순위 | 항목 | 작업량 | 효과 |
|----------|------|--------|------|
| 🔴 높음 | CT-02 참조위험률 → `KidiClient.getReferenceLossRates()` | 소 | KIDI 연동 시 교체 용이 |
| 🔴 높음 | CT-05 REQUIRED_DOCS → `KidiClient.getRequiredVerificationDocs()` | 소 | 동일 |
| 🔴 높음 | CT-06 REQUIRED_DOCS → `FssClient.getRequiredSaleDocs()` | 소 | FSS 연동 시 교체 용이 |
| 🟡 중간 | CT-03 DOC_NAMES → `DocType.displayName` enum 필드 | 소 | 배열 2개 → enum 1개 |
| 🟡 중간 | CT-02 기본 보험료 1,150,000 → 담보 구성 기반 계산 | 대 | 정확도 향상 |
| 🟢 낮음 | CT-02 준비금 비율 0.40 → 상수명 명시 | 최소 | 가독성 |
| 🟢 낮음 | CT-02 NPV 파라미터 → `ProfitabilityCalculator` 문서화 | 최소 | 유지보수성 |

---

## 구조 다이어그램 (현재 → 목표)

```
현재                              목표
─────────────────────────────     ──────────────────────────────────
CT-03                             CT-03
  └─ String[] DOC_NAMES    →        └─ DocType.displayName (enum)

CT-05                             CT-05
  └─ String[] REQUIRED_DOCS →       └─ KidiClient.getRequiredVerificationDocs()

CT-06                             CT-06
  └─ String[] REQUIRED_DOCS →       └─ FssClient.getRequiredSaleDocs()

CT-02                             CT-02
  └─ "78.5%", "68.2%" (인라인) →    └─ KidiClient.getReferenceLossRates()
```
