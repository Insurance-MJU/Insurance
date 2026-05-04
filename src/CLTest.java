import infra.Context;
import ui.employee.CL01AccidentRegistration;
import ui.employee.CL02DamageAssessment;
import ui.employee.CL03DamageInvestigation;
import ui.employee.CL04InsurancePayment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * CL01~CL04 통합 테스트 — ClaimRepository 스텁 데이터 연동 확인
 *
 * 실행:
 *   javac -encoding UTF-8 -sourcepath src -d out src/CLTest.java
 *   java  -cp out CLTest
 */
public class CLTest {

    private static final PrintStream STDOUT = System.out;
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        STDOUT.println("===== CL 유즈케이스 통합 테스트 시작 =====\n");

        testCL01_AccidentRegistration();
        testCL03_Standalone();
        testCL04_Standalone();
        testCL02_FullFlow();

        STDOUT.println("\n===== 결과 =====");
        STDOUT.println("통과: " + passed + " / " + (passed + failed));
        if (failed > 0) {
            STDOUT.println("실패: " + failed);
            System.exit(1);
        }
    }

    // ── 헬퍼 ─────────────────────────────────────────────────

    /** Scanner를 가짜 입력으로 교체 후 유즈케이스 실행, 출력을 문자열로 반환 */
    private static String run(Runnable useCase, String input) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes("UTF-8"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        System.setIn(bais);
        System.setOut(new PrintStream(baos, true, "UTF-8"));
        Context.getInstance().setScanner(new Scanner(bais));

        try {
            useCase.run();
        } finally {
            System.setOut(STDOUT);
        }

        return baos.toString("UTF-8");
    }

    private static void check(String label, String out, String... keywords) {
        for (String kw : keywords) {
            if (!out.contains(kw)) {
                STDOUT.println("[FAIL] " + label);
                STDOUT.println("       출력에 없음 → \"" + kw + "\"");
                failed++;
                return;
            }
        }
        STDOUT.println("[PASS] " + label);
        passed++;
    }

    // ── CL-01 ────────────────────────────────────────────────

    static void testCL01_AccidentRegistration() throws Exception {
        STDOUT.println("[CL-01] 사고를 접수한다");

        String input = lines(
            "2026-04-19",    // 기간
            "미처리",          // 상태
            "홍길동",          // 고객명 (상세 조회)
            "010-1234-5678", // 전화번호
            "SEOUL-01",      // 지역 코드
            "자동차 대물",     // 전문 분야
            "EMP-1023",      // 담당 직원
            ""               // Enter → 메인 메뉴 복귀
        );

        String out = run(() -> new CL01AccidentRegistration().run(), input);

        // Step 4: 레포 사고 목록
        check("CL01 ① 미처리 사고 목록(홍길동/자동차 대물 사고)",
            out, "홍길동", "자동차 대물 사고");

        // Step 6: 레포 사고 상세 → 계약 원장
        check("CL01 ② 사고 경위 및 서류(신호 대기 중 후방 추돌)",
            out, "신호 대기 중 후방 추돌 사고 발생",
            "사고현장사진.jpg", "차량수리견적서.pdf");
        check("CL01 ③ 계약 원장(CNT-20240315-001 / 자동차 대물)",
            out, "CNT-20240315-001", "자동차 대물");

        // Step 10: 청구 등록 완료
        check("CL01 ④ 담당자 배당 완료(EMP-1023 / CL-00002)",
            out, "EMP-1023", "CL-00002");

        STDOUT.println();
    }

    // ── CL-03 (단독) ─────────────────────────────────────────

    static void testCL03_Standalone() throws Exception {
        STDOUT.println("[CL-03] 손해를 조사한다 (단독)");

        String input = lines(
            "ACC-2026-002",    // 사고 접수 번호 (김철수, 자기차량손해 3,000만원)
            "주차장 파손 현장 확인", // 현장 조사 소견
            "CAR-D-02",        // 파손 부위 코드
            "5",               // 부상 급수
            "60",              // 당사 과실
            "40",              // 타사 과실
            "부책",             // 면/부책
            "합의금 산출 진행 요망" // 최종 조사 의견
        );

        String out = run(() -> new CL03DamageInvestigation().run(), input);

        // Step 4: 레포 보상 한도 반영
        check("CL03 ① 레포 보상 한도(3,000만원) 출력",
            out, "3,000만원");

        // Step 8: 조사 내역 취합
        check("CL03 ② 조사 내역 (파손코드/부상급수/과실비율)",
            out, "CAR-D-02", "5급", "60%", "40%", "부책");

        // Step 10: 저장 완료
        check("CL03 ③ 조사 내역 저장 완료",
            out, "조사 내역이 저장되었습니다");

        STDOUT.println();
    }

    // ── CL-04 (단독) ─────────────────────────────────────────

    static void testCL04_Standalone() throws Exception {
        STDOUT.println("[CL-04] 보험금을 지급한다 (단독 — 사전 적재 CL-00001)");

        // ClaimRepository static 블록에 CL-00001(ACC-2026-003, 이영희, 1480만원, 지급대기) 존재
        String input = lines(
            "ACC-2026-003",      // 접수 번호
            "한국은행",             // 수령 은행명
            "123-456-7890",      // 계좌 번호
            "의견서_ACC003.pdf",  // 결재용 사정의견서
            "secret1234",        // 결제 인증 비밀번호
            "Y"                  // 이체 완료 → 사고 처리 종결
        );

        String out = run(() -> new CL04InsurancePayment().run(), input);

        // Step 1: 지급 대기 목록 (pre-seeded)
        check("CL04 ① 지급 대기 목록(이영희 / 1480만원)",
            out, "이영희", "1480만원");

        // Step 5: 계좌 검증 완료
        check("CL04 ② 예금주 실명 검증 완료",
            out, "검증 완료");

        // Step 11: 종결 처리
        check("CL04 ③ 지급 종결 및 알림톡 발송",
            out, "지급 종결", "발송 완료");

        STDOUT.println();
    }

    // ── CL-02 (CL-03 include + CL-04 extend) ────────────────

    static void testCL02_FullFlow() throws Exception {
        STDOUT.println("[CL-02] 손해액을 산정한다 (CL-03 include + CL-04 extend)");

        // 전제: CL01 테스트가 ACC-2026-001 / CL-00002(처리중) 생성 완료
        String input = lines(
            // CL-02 Step 2
            "EMP-1023",           // 담당자 사번
            "ACC-2026-001",       // 접수 번호
            "Y",                  // 조사 진행 여부

            // ── <<include>> CL-03 ──
            "ACC-2026-001",       // 사고 접수 번호
            "후방 추돌 현장 확인",   // 현장 조사 소견
            "CAR-D-01",           // 파손 부위 코드
            "12",                 // 부상 급수
            "80",                 // 당사 과실
            "20",                 // 타사 과실
            "부책",                // 면/부책
            "합의금 산출 진행 요망",  // 최종 조사 의견

            // CL-02 Step 7 (합의금·자기부담금)
            "1500",               // 최종 합의금
            "20",                 // 자기부담금
            "합의 완료 승인",       // 담당자 결제 의견
            "",                   // Enter → CL-04 실행

            // ── <<extend>> CL-04 ──
            "ACC-2026-001",       // 접수 번호
            "신한은행",             // 수령 은행명
            "987-654-3210",       // 계좌 번호
            "의견서_ACC001.pdf",   // 결재용 사정의견서
            "secret9876",         // 결제 인증 비밀번호
            "Y",                  // 이체 완료 → 사고 처리 종결

            ""                    // Enter → 메인 메뉴 복귀
        );

        String out = run(() -> new CL02DamageAssessment().run(), input);

        // Step 4: 레포 사고 초기 접수 내역
        check("CL02 ① 레포 사고 정보(자동차 대물 사고 / 서울 강남구 / 소나타)",
            out,
            "자동차 대물 사고",
            "서울 강남구 테헤란로",
            "12가 3456 (현대 소나타)");

        // Step 6: 레포 보상 한도액
        check("CL02 ② 보상 한도액(2,000만원)",
            out, "2,000만원");

        // CL-03 결과 (included)
        check("CL02 ③ CL-03 조사 결과 저장(CAR-D-01 / 조사 내역 저장)",
            out, "CAR-D-01", "조사 내역이 저장되었습니다");

        // Step 8: 최종 결정 손해액
        check("CL02 ④ 최종 손해액(합의 1500 - 자기부담 20 = 1480만원)",
            out, "1500만원", "1480만원");

        // CL-04 결과 (extended)
        check("CL02 ⑤ CL-04 지급 종결",
            out, "지급 종결", "보험금이 지급 완료되었습니다");

        STDOUT.println();
    }

    // ── 유틸 ─────────────────────────────────────────────────

    private static String lines(String... parts) {
        return String.join("\n", parts) + "\n";
    }
}
