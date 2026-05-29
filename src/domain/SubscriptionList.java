package domain;

import common.exception.domain.NotFoundException;
import infra.dao.SubscriptionDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionList {
    private final SubscriptionDao dao;
    private final List<Subscription> subscriptions;

    public SubscriptionList(SubscriptionDao dao) {
        this.dao = dao;
        this.subscriptions = Collections.emptyList();
    }

    public SubscriptionList(List<Subscription> subscriptions) {
        this.dao = null;
        this.subscriptions = Collections.unmodifiableList(subscriptions);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public SubscriptionList findAll() {
        return dao.findAll();
    }

    public SubscriptionList findPendingReview() {
        return dao.findPendingReview();
    }

    public SubscriptionList findByApplicantName(String name) {
        return dao.findByApplicantName(name);
    }

    public SubscriptionList findByUserId(String userId) {
        return dao.findByUserId(userId);
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Subscription> getAll() { return subscriptions; }
    public boolean isEmpty() { return subscriptions.isEmpty(); }
    public int size() { return subscriptions.size(); }

    public SubscriptionList excludeApproved() {
        return new SubscriptionList(
            subscriptions.stream()
                .filter(s -> s.getStatus() != SubscriptionStatus.APPROVED)
                .collect(Collectors.toList())
        );
    }

    public int pendingCount() {
        return (int) subscriptions.stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.PENDING_REVIEW)
            .count();
    }

    public Subscription getByNo(String subscriptionNo) {
        Subscription s = findByNo(subscriptionNo);
        if (s == null) throw new NotFoundException("청약을 찾을 수 없습니다: " + subscriptionNo);
        return s;
    }

    public Subscription findByNo(String subscriptionNo) {
        if (!subscriptions.isEmpty()) {
            return subscriptions.stream()
                .filter(s -> subscriptionNo.equals(s.getSubscriptionNo()))
                .findFirst()
                .orElse(null);
        }
        return dao.findByNo(subscriptionNo);
    }

    public String nextSubscriptionNo() {
        return dao.nextSubscriptionNo();
    }

    public void save(Subscription s) {
        dao.save(s);
    }
}
