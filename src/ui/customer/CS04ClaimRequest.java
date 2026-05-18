package ui.customer;

import domain.Accident;
import domain.Contract;
import infra.Context;
import infra.external.IdentityVerificationService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class CS04ClaimRequest {
    private final Scanner sc = Context.getInstance().scanner();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CS-04: 보험금을 청구한다");
        System.out.println("========================================");

        // Step 2~4: 본인 인증 (외부 시스템)
        IdentityVerificationService.AuthResult auth =
            new IdentityVerificationService(sc).verify();
        String authName  = auth.name;
        String authPhone = auth.phone;

        // Step 5: 계약 조회 동의
        System.out.print("\n계약 조회에 동의하십니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 계약 조회 동의가 필요합니다.");
            returnToMenu();
            return;
        }

        // <<include>> CS-05: CS-04 인증이 완료되었으므로 authName을 직접 전달(중복 인증 제거)
        // A1: 청구 대상 계약 미선택
        Contract selectedContract = new CS05ContractInquiry().runAsInclude(authName);
        if (selectedContract == null) {
            System.out.println("\n[경고] 대상 보험 계약은 필수 선택 사항입니다. 대상을 리스트에 추가해 주세요.");
            returnToMenu();
            return;
        }

        // Step 6~7: 사고 기초 정보 입력
        System.out.println("\n[사고 기초 정보 입력]");
        System.out.print(" 사고 발생 일시 (예: 2026-04-19 14:00): ");
        String accidentDate = sc.nextLine().trim();
        System.out.print(" 사고 장소 (예: 서울시 강남구): ");
        String accidentPlace = sc.nextLine().trim();
        System.out.print(" 상세 경위 (예: 후방 추돌): ");
        String accidentDetail = sc.nextLine().trim();

        // E1: 사고 발생 일시가 보험 가입 기간 외인 경우 — 계약의 실제 기간과 비교
        if (!isAccidentDateValid(accidentDate, selectedContract)) {
            System.out.println("\n[오류] 입력된 사고 일시 값이 허용 범위를 초과하였습니다.");
            System.out.println("       사고 일시는 보험계약 기간(" + selectedContract.getIssueDateString()
                + " ~ " + formatDate(selectedContract.getEndDate()) + ") 내여야 합니다.");
            System.out.println("       사고 일시를 수정하고 다시 시도해주세요.");
            returnToMenu();
            return;
        }

        // Step 8~9: 증빙 서류 업로드
        System.out.println("\n[증빙 서류 업로드]");
        System.out.print(" 진단서 파일명 (예: 진단서_김고객.jpg): ");
        String doc1 = sc.nextLine().trim();
        System.out.print(" 현장사진 파일명 (예: 현장_1.jpg): ");
        String doc2 = sc.nextLine().trim();
        System.out.println("[서류 제출]");
        System.out.println("\n[첨부 파일 확인]");
        System.out.println(" - " + doc1 + "  (정상 첨부)");
        System.out.println(" - " + doc2 + "  (정상 첨부)");

        // Step 10~11: 최종 제출 동의
        System.out.println("\n[최종 제출 동의]");
        System.out.print(" 위 내용으로 보험금을 청구하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 청구가 취소되었습니다.");
            returnToMenu();
            return;
        }

        // Step 12: 사고 접수 후 보상팀 이관
        Accident accident = Accident.report(
            Accident.nextId(),
            authName,
            authPhone,
            accidentDate,
            accidentPlace,
            accidentDetail,
            doc1 + "," + doc2,
            selectedContract
        );
        accident.transferToCompensation();
        accident.save();

        System.out.println("\n[보험금 청구 완료]");
        System.out.println("------------------------------------------------------------");
        System.out.println(" 접수 번호   : " + accident.getAccidentId());
        System.out.println(" 사고 일시   : " + accident.getAccidentDateDisplay());
        System.out.println(" 사고 장소   : " + accident.getAccidentLocation());
        System.out.println(" 경위        : " + accident.getAccidentDetail());
        System.out.println(" 처리 상태   : " + accident.getStatusLabel());
        System.out.println(" 안내        : 담당자가 배정되면 연락드리겠습니다. (1~3 영업일 소요)");
        System.out.println("------------------------------------------------------------");
        System.out.println("\n보험금 청구가 완료되었습니다.");

        returnToMenu();
    }

    /**
     * E1: 사고 일시가 계약 기간 내에 있는지 검증.
     * - 빈 문자열이면 false
     * - 파싱 불가 형식이면 false
     * - 계약 startDate 이전이면 false
     * - 계약 endDate 이후이면 false (endDate가 null이면 상한 없음)
     */
    private boolean isAccidentDateValid(String accidentDate, Contract contract) {
        if (accidentDate == null || accidentDate.isEmpty()) return false;
        try {
            Date accDate = SDF.parse(accidentDate);
            Date contractStart = contract.getStartDate();
            Date contractEnd   = contract.getEndDate();
            if (contractStart != null && accDate.before(contractStart)) return false;
            if (contractEnd   != null && accDate.after(contractEnd))    return false;
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "무기한";
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
