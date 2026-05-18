package infra.dao;

import domain.Accident;
import domain.AccidentStatus;
import domain.common.Money;
import infra.util.FileStore;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AccidentDao {
    private static final AccidentDao INSTANCE = new AccidentDao();
    public static AccidentDao getInstance() { return INSTANCE; }

    private static final List<Accident> STORE;
    static {
        List<Accident> loaded = FileStore.load("accidents.dat");
        if (loaded != null) { STORE = loaded; }
        else { STORE = new ArrayList<>(); initDefaults(); }
    }

    private static void initDefaults() {
        Accident a1 = new Accident("ACC-2026-001", "2026-04-19 09:32", "홍길동", "010-1234-5678",
            "자동차 대물 사고", "서울 강남구 테헤란로", "신호 대기 중 후방 추돌 사고 발생",
            "사고현장사진.jpg,차량수리견적서.pdf", "CNT-20240315-001", "자동차 대물",
            new Money(20_000_000, "KRW"), "12가 3456 (현대 소나타)", AccidentStatus.PENDING);
        a1.setPersonalInjuryLimit(new Money(10_000_000, "KRW"));
        a1.setExpectedRepairCost(new Money(850_000, "KRW"));
        a1.setRegionCode("SEOUL-01");
        STORE.add(a1);

        Accident a2 = new Accident("ACC-2026-002", "2026-04-19 11:15", "김철수", "010-9876-5432",
            "차량 파손", "경기도 수원시 팔달구", "주차장 내 차량 문 충돌로 인한 파손",
            "차량파손사진.jpg,수리견적서.pdf", "CNT-20240520-002", "자기차량손해",
            new Money(30_000_000, "KRW"), "34나 5678 (기아 K5)", AccidentStatus.PENDING);
        a2.setPersonalInjuryLimit(new Money(20_000_000, "KRW"));
        a2.setExpectedRepairCost(new Money(1_200_000, "KRW"));
        a2.setRegionCode("GYEONGGI-01");
        STORE.add(a2);

        Accident a3 = new Accident("ACC-2026-003", "2026-04-18 14:20", "이영희", "010-5555-1234",
            "차량 전손", "인천시 부평구 경인로", "교차로 신호 위반으로 인한 정면 충돌",
            "사고사진.jpg,전손감정서.pdf", "CNT-20231210-003", "자기차량손해",
            new Money(50_000_000, "KRW"), "56다 9012 (현대 그랜저)", AccidentStatus.IN_PROGRESS);
        a3.setPersonalInjuryLimit(new Money(30_000_000, "KRW"));
        a3.setExpectedRepairCost(new Money(3_500_000, "KRW"));
        a3.setRegionCode("INCHEON-01");
        STORE.add(a3);

        FileStore.save("accidents.dat", STORE);
    }

    public List<Accident> findByDateAndStatus(String date, String status) {
        return STORE.stream()
            .filter(a -> a.getAccidentDate() != null
                && new SimpleDateFormat("yyyy-MM-dd").format(a.getAccidentDate()).startsWith(date))
            .filter(a -> status.isEmpty() || (a.getStatus() != null && a.getStatus().getLabel().equals(status)))
            .collect(Collectors.toList());
    }

    public List<Accident> findPendingAccidents() {
        return STORE.stream().filter(a -> a.getStatus() == AccidentStatus.PENDING).collect(Collectors.toList());
    }

    public Accident findById(String accidentId) {
        return STORE.stream().filter(a -> a.getAccidentId().equals(accidentId)).findFirst().orElse(null);
    }

    public Accident findByCustomerName(String name) {
        return STORE.stream().filter(a -> a.getReportedBy().equals(name)).findFirst().orElse(null);
    }

    public void save(Accident a) {
        STORE.removeIf(x -> x.getAccidentId().equals(a.getAccidentId()));
        STORE.add(a);
        FileStore.save("accidents.dat", STORE);
    }

    public String nextId() {
        return String.format("ACC-2026-%03d", STORE.size() + 1);
    }
}
