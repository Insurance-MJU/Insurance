package domain;

import common.exception.domain.NotFoundException;
import domain.common.Money;
import infra.dao.SubscriptionDao;
import infra.vo.SubscriptionVO;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionList {
    private final SubscriptionDao dao;
    private final List<Subscription> subscriptions;

    public SubscriptionList(SubscriptionDao dao) {
        this.dao           = dao;
        this.subscriptions = Collections.emptyList();
    }

    public SubscriptionList(List<Subscription> subscriptions) {
        this.dao           = null;
        this.subscriptions = Collections.unmodifiableList(subscriptions);
    }

    private static Subscription toDomain(SubscriptionVO vo) {
        if (vo == null) return null;
        String subDate = vo.subscriptionDate != null
            ? new SimpleDateFormat("yyyy-MM-dd").format(vo.subscriptionDate)
            : new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int safeAge = Math.max(18, vo.age);
        Subscription s = Subscription.register(
            vo.subscriptionNo, vo.applicantName, vo.ssn, vo.address, vo.carNumber,
            vo.chassisNumber, vo.productName,
            new Money(vo.premium, "KRW"), new Money(vo.basePremium, "KRW"),
            subDate, vo.occupation, safeAge, vo.coveragesDescription
        );
        s.setUserId(vo.userId);
        if (vo.status != null) {
            SubscriptionStatus status = SubscriptionStatus.valueOf(vo.status);
            if (status == SubscriptionStatus.APPROVED)            s.approve();
            else if (status == SubscriptionStatus.REJECTED)       s.reject(vo.rejectReason != null ? vo.rejectReason : "");
            else if (status == SubscriptionStatus.SUPPLEMENT_REQUIRED) s.requestSupplement(vo.supplementDocuments != null ? vo.supplementDocuments : "");
        }
        return s;
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public SubscriptionList findAll() {
        return new SubscriptionList(dao.findAll().stream().map(SubscriptionList::toDomain).collect(Collectors.toList()));
    }

    public SubscriptionList findPendingReview() {
        return new SubscriptionList(dao.findPendingReview().stream().map(SubscriptionList::toDomain).collect(Collectors.toList()));
    }

    public SubscriptionList findByApplicantName(String name) {
        return new SubscriptionList(dao.findByApplicantName(name).stream().map(SubscriptionList::toDomain).collect(Collectors.toList()));
    }

    public SubscriptionList findByUserId(String userId) {
        return new SubscriptionList(dao.findByUserId(userId).stream().map(SubscriptionList::toDomain).collect(Collectors.toList()));
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
        return toDomain(dao.findByNo(subscriptionNo));
    }

    public String nextSubscriptionNo() {
        return dao.nextSubscriptionNo();
    }

    public void save(Subscription s) {
        dao.save(new SubscriptionVO(
            s.getSubscriptionNo(), s.getUserId(), s.getApplicantName(), s.getSsn(),
            s.getAddress(), s.getCarNumber(), s.getChassisNumber(), s.getProductName(),
            s.getPremium()     != null ? s.getPremium().getAmount()     : 0L,
            s.getBasePremium() != null ? s.getBasePremium().getAmount() : 0L,
            s.getSubscriptionDate(),
            s.getStatus() != null ? s.getStatus().name() : null,
            s.getOccupation(), s.getAge(), s.getCoveragesDescription(),
            s.getRejectReason(), s.getSupplementDocuments()
        ));
    }
}
