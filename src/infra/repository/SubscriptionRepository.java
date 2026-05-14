package infra.repository;

import domain.contract.Subscription;
import domain.common.Money;
import infra.util.FileStore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionRepository {
    private static final List<Subscription> STORE;

    static {
        List<Subscription> loaded = FileStore.load("subscriptions.dat");
        if (loaded != null) {
            STORE = loaded;
        } else {
            STORE = new ArrayList<>();
            initDefaults();
        }
    }

    private static void initDefaults() {
        STORE.add(Subscription.register(
            "20260401-0001", "박수현", "020101-3******",
            "서울시 강남구", "64마0866", "KMHCT41DBLU123",
            "MZ세대 다이렉트 차보험",
            new Money(2_907_200L, "KRW"),
            new Money(2_794_010L, "KRW"),
            "2026-04-01", "대학생", 24,
            "대인I/II, 대물 5억, 자상 1억, 무보험 2억, 자차 가입"
        ));
        FileStore.save("subscriptions.dat", STORE);
    }

    public static List<Subscription> findAll() {
        return new ArrayList<>(STORE);
    }

    public static List<Subscription> findPendingReview() {
        return STORE.stream()
            .filter(Subscription::isPendingReview)
            .collect(Collectors.toList());
    }

    public static Subscription findByNo(String subscriptionNo) {
        return STORE.stream()
            .filter(s -> s.getSubscriptionNo().equals(subscriptionNo))
            .findFirst().orElse(null);
    }

    public static void save(Subscription subscription) {
        STORE.removeIf(s -> s.getSubscriptionNo().equals(subscription.getSubscriptionNo()));
        STORE.add(subscription);
        FileStore.save("subscriptions.dat", STORE);
    }
}
