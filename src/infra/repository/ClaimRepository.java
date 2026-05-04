package infra.repository;

import domain.Accident;
import domain.Claim;
import domain.DamageInvestigation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimRepository {

    private static final List<Accident> ACCIDENT_STORE = new ArrayList<>();
    private static final List<Claim> CLAIM_STORE = new ArrayList<>();
    private static final List<DamageInvestigation> INVESTIGATION_STORE = new ArrayList<>();

    static {
        ACCIDENT_STORE.add(new Accident(
            "ACC-2026-001", "2026-04-19 09:32",
            "홍길동", "010-1234-5678",
            "자동차 대물 사고",
            "서울 강남구 테헤란로",
            "신호 대기 중 후방 추돌 사고 발생",
            "사고현장사진.jpg,차량수리견적서.pdf",
            "CNT-20240315-001",
            "자동차 대물", "2,000만원",
            "12가 3456 (현대 소나타)",
            "미처리"
        ));
        ACCIDENT_STORE.add(new Accident(
            "ACC-2026-002", "2026-04-19 11:15",
            "김철수", "010-9876-5432",
            "차량 파손",
            "경기도 수원시 팔달구",
            "주차장 내 차량 문 충돌로 인한 파손",
            "차량파손사진.jpg,수리견적서.pdf",
            "CNT-20240520-002",
            "자기차량손해", "3,000만원",
            "34나 5678 (기아 K5)",
            "미처리"
        ));
        ACCIDENT_STORE.add(new Accident(
            "ACC-2026-003", "2026-04-18 14:20",
            "이영희", "010-5555-1234",
            "차량 전손",
            "인천시 부평구 경인로",
            "교차로 신호 위반으로 인한 정면 충돌",
            "사고사진.jpg,전손감정서.pdf",
            "CNT-20231210-003",
            "자기차량손해", "5,000만원",
            "56다 9012 (현대 그랜저)",
            "처리중"
        ));

        Claim c = new Claim(
            "CL-00001", "ACC-2026-003",
            "이영희", "2026-04-18",
            "CNT-20231210-003",
            "차량 전손", "지급대기"
        );
        c.setAssignedEmployee("EMP-1023");
        c.setSettlement(1480);
        c.setDeductible(0);
        c.setCompensationAmount(1480);
        CLAIM_STORE.add(c);
    }

    public static List<Accident> findByDateAndStatus(String date, String status) {
        return ACCIDENT_STORE.stream()
            .filter(a -> a.getAccidentDate().startsWith(date))
            .filter(a -> status.isEmpty() || a.getStatus().equals(status))
            .collect(Collectors.toList());
    }

    public static List<Accident> findPendingAccidents() {
        return ACCIDENT_STORE.stream()
            .filter(a -> "미처리".equals(a.getStatus()))
            .collect(Collectors.toList());
    }

    public static Accident findAccidentById(String accidentId) {
        return ACCIDENT_STORE.stream()
            .filter(a -> a.getAccidentId().equals(accidentId))
            .findFirst().orElse(null);
    }

    public static Accident findAccidentByCustomerName(String name) {
        return ACCIDENT_STORE.stream()
            .filter(a -> a.getReportedBy().equals(name))
            .findFirst().orElse(null);
    }

    public static Claim findClaimByAccidentId(String accidentId) {
        return CLAIM_STORE.stream()
            .filter(c -> c.getAccidentId().equals(accidentId))
            .findFirst().orElse(null);
    }

    public static Claim findClaimById(String claimId) {
        return CLAIM_STORE.stream()
            .filter(c -> c.getClaimId().equals(claimId))
            .findFirst().orElse(null);
    }

    public static List<Claim> findClaimsAwaitingPayment() {
        return CLAIM_STORE.stream()
            .filter(c -> "지급대기".equals(c.getStatus()))
            .collect(Collectors.toList());
    }

    public static void saveAccident(Accident accident) {
        ACCIDENT_STORE.removeIf(a -> a.getAccidentId().equals(accident.getAccidentId()));
        ACCIDENT_STORE.add(accident);
    }

    public static void saveClaim(Claim claim) {
        CLAIM_STORE.removeIf(c -> c.getClaimId().equals(claim.getClaimId()));
        CLAIM_STORE.add(claim);
    }

    public static void saveInvestigation(DamageInvestigation inv) {
        INVESTIGATION_STORE.removeIf(i -> i.getAccidentId().equals(inv.getAccidentId()));
        INVESTIGATION_STORE.add(inv);
    }

    public static void updateAccidentStatus(String accidentId, String status) {
        ACCIDENT_STORE.stream()
            .filter(a -> a.getAccidentId().equals(accidentId))
            .findFirst()
            .ifPresent(a -> a.setStatus(status));
    }

    public static void updateClaimStatus(String claimId, String status) {
        CLAIM_STORE.stream()
            .filter(c -> c.getClaimId().equals(claimId))
            .findFirst()
            .ifPresent(c -> c.setStatus(status));
    }

    public static String nextClaimId() {
        return String.format("CL-%05d", CLAIM_STORE.size() + 1);
    }

    public static String nextAccidentId() {
        return String.format("ACC-2026-%03d", ACCIDENT_STORE.size() + 1);
    }
}
