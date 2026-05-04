package infra.repository;

import domain.Accident;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccidentRepository {

    private static final List<Accident> STORE = new ArrayList<>();

    static {
        Accident a1 = new Accident(
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
        );
        a1.setPersonalInjuryLimit("1,000만원");
        a1.setExpectedRepairCost("850,000원");
        STORE.add(a1);

        Accident a2 = new Accident(
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
        );
        a2.setPersonalInjuryLimit("2,000만원");
        a2.setExpectedRepairCost("1,200,000원");
        STORE.add(a2);

        Accident a3 = new Accident(
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
        );
        a3.setPersonalInjuryLimit("3,000만원");
        a3.setExpectedRepairCost("3,500,000원");
        STORE.add(a3);
    }

    public static List<Accident> findByDateAndStatus(String date, String status) {
        return STORE.stream()
            .filter(a -> a.getAccidentDate().startsWith(date))
            .filter(a -> status.isEmpty() || a.getStatus().equals(status))
            .collect(Collectors.toList());
    }

    public static List<Accident> findPendingAccidents() {
        return STORE.stream()
            .filter(a -> "미처리".equals(a.getStatus()))
            .collect(Collectors.toList());
    }

    public static Accident findById(String accidentId) {
        return STORE.stream()
            .filter(a -> a.getAccidentId().equals(accidentId))
            .findFirst().orElse(null);
    }

    public static Accident findByCustomerName(String name) {
        return STORE.stream()
            .filter(a -> a.getReportedBy().equals(name))
            .findFirst().orElse(null);
    }

    public static void save(Accident accident) {
        STORE.removeIf(a -> a.getAccidentId().equals(accident.getAccidentId()));
        STORE.add(accident);
    }

    public static void updateStatus(String accidentId, String status) {
        STORE.stream()
            .filter(a -> a.getAccidentId().equals(accidentId))
            .findFirst()
            .ifPresent(a -> a.setStatus(status));
    }

    public static String nextId() {
        return String.format("ACC-2026-%03d", STORE.size() + 1);
    }
}
