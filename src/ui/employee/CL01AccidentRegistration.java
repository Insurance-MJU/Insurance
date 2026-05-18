package ui.employee;

import domain.Accident;
import domain.AccidentStatus;
import domain.Claim;
import domain.ClaimStatus;
import domain.Employee;
import infra.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CL01AccidentRegistration {
    private final Scanner sc = Context.getInstance().scanner();

    public void run() {
        System.out.println("\n[CL-01] 사고를 접수한다");
        System.out.println("========================================");

        // Step 2: 신규사고접수 입력란 출력
        String period;
        String status;

        // Step 3 + E1: 형식 오류 시 재입력
        while (true) {
            System.out.println("\n[ 신규 사고 접수 조회 ]");
            System.out.print("기간 (예: 2026-04-19): ");
            period = sc.nextLine().trim();
            System.out.print("상태 (예: 미처리 / 처리중 / 완료): ");
            status = sc.nextLine().trim();
            System.out.println("[조회]");

            // E1: 날짜 형식 허용 범위 초과
            if (!period.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("\n[오류] >>> 기간 <<< 입력된 사고 정보의 값이 허용 범위를 초과하였습니다.");
                System.out.println("       날짜 형식을 확인해 주세요. (예: 2026-04-19)\n");
                continue;
            }
            break;
        }

        // Step 4: 레포지토리에서 사고 목록 조회
        List<Accident> accidents = Accident.findByDateAndStatus(period, status);

        System.out.println("\n[ 미처리 사고 청구 목록 ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-22s %-12s %-20s%n", "접수 일시", "고객명", "청구 사유");
        System.out.println("------------------------------------------------------------");
        if (accidents.isEmpty()) {
            System.out.println("  조회된 사고 접수 건이 없습니다.");
        } else {
            for (Accident a : accidents) {
                System.out.printf("%-22s %-12s %-20s%n",
                    a.getAccidentDateDisplay(), a.getReportedBy(), a.getDescription());
            }
        }
        System.out.println("------------------------------------------------------------");

        // Step 5 + A1: 고객명·전화번호 필수 검색 조건 검증
        String customerName;
        String phone;

        while (true) {
            System.out.println("\n[ 상세 조회 ]");
            System.out.print("고객명: ");
            customerName = sc.nextLine().trim();
            System.out.print("전화번호: ");
            phone = sc.nextLine().trim();
            System.out.println("[상세 조회]");

            // A1: 필수 조건 누락
            if (customerName.isEmpty() || phone.isEmpty()) {
                System.out.println("\n[경고] 고객명과 전화번호는 필수 검색 조건입니다. 검색 조건을 추가해 주세요.\n");
                continue;
            }
            break;
        }

        // Step 6: 레포지토리에서 사고 상세 정보 조회
        Accident accident = Accident.findByCustomerName(customerName);

        System.out.println("\n[ 사고 상세 정보 - " + customerName + " / " + phone + " ]");
        System.out.println("------------------------------------------------------------");
        if (accident != null) {
            System.out.println("[제출된 사고 경위서]");
            System.out.println("  - 발생일시: " + accident.getAccidentDateDisplay());
            System.out.println("  - 사고 내용: " + accident.getAccidentDetail());
            System.out.println("[증빙 서류 뷰어]");
            for (String doc : accident.getDocuments().split(",")) {
                System.out.println("  - " + doc.trim());
            }
            System.out.println("[계약 원장 정보]");
            System.out.println("  - 계약번호: " + accident.getContractId());
            String limitDisplay = accident.getCoverageLimit() != null
                ? accident.getCoverageLimit().getAmount() / 10_000 + "만원" : "-";
            System.out.println("  - 담보: " + accident.getCoverageDescription() + " / 한도: " + limitDisplay);
        } else {
            System.out.println("  [해당 고객의 사고 접수 정보를 찾을 수 없습니다]");
        }
        System.out.println("------------------------------------------------------------");

        // Step 7: 배당 담당자 검색 조건 입력
        String regionCode = (accident != null && accident.getRegionCode() != null)
            ? accident.getRegionCode() : "-";
        System.out.println("\n[ 배당 담당자 검색 ]");
        System.out.println("사고 발생 지역 코드: " + regionCode);
        System.out.print("전문 분야 (예: 자동차 대물): ");
        String specialty = sc.nextLine().trim();
        System.out.println("[배당 담당자 검색]");

        // Step 8: 현장조사역 후보 목록 출력
        System.out.println("\n[ 현장조사역 후보 목록 - " + regionCode + " / " + specialty + " ]");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-15s %-14s %-10s%n", "직원 번호", "직원명", "미결 건수");
        System.out.println("------------------------------------------------------------");
        List<Employee.FieldInvestigator> investigators =
            Employee.findBySpecialty(specialty);
        if (investigators.isEmpty()) {
            System.out.println("  해당 조건에 맞는 현장조사역이 없습니다.");
        } else {
            for (Employee.FieldInvestigator emp : investigators) {
                System.out.printf("%-15s %-14s %-10s%n",
                    emp.getEmployeeId(), emp.getName(), emp.getOpenCaseCount() + "건");
            }
        }
        System.out.println("------------------------------------------------------------");

        // Step 9: 담당자 배당 확정
        System.out.println("\n[ 배당 및 접수 확정 ]");
        System.out.print("담당자 직원 번호: ");
        String empNo = sc.nextLine().trim();
        System.out.println("[배당 및 접수 확정]");

        // Step 10: 레포지토리에 Claim 저장 및 Accident 상태 업데이트
        String claimId = Claim.nextId();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        if (accident != null) {
            Claim claim = new Claim(
                claimId, accident,
                customerName, now,
                accident.getContractId(),
                accident.getDescription(), ClaimStatus.INVESTIGATING
            );
            claim.setAssignedEmployee(empNo);
            claim.save();
            accident.setStatus(AccidentStatus.IN_PROGRESS);
            accident.save();
        }

        System.out.println("\n담당자가 배당되었습니다.");
        System.out.println("  - 배당 직원: " + empNo);
        System.out.println("  - 접수 번호: " + claimId);

        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
