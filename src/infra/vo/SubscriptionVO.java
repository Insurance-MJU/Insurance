package infra.vo;

import java.util.Date;

public class SubscriptionVO {
    public final String subscriptionNo;
    public final String userId;
    public final String applicantName;
    public final String ssn;
    public final String address;
    public final String carNumber;
    public final String chassisNumber;
    public final String productName;
    public final long premium;
    public final long basePremium;
    public final Date subscriptionDate;
    public final String status;
    public final String occupation;
    public final int age;
    public final String coveragesDescription;
    public final String rejectReason;
    public final String supplementDocuments;

    public SubscriptionVO(String subscriptionNo, String userId, String applicantName, String ssn,
                          String address, String carNumber, String chassisNumber, String productName,
                          long premium, long basePremium, Date subscriptionDate, String status,
                          String occupation, int age, String coveragesDescription,
                          String rejectReason, String supplementDocuments) {
        this.subscriptionNo       = subscriptionNo;
        this.userId               = userId;
        this.applicantName        = applicantName;
        this.ssn                  = ssn;
        this.address              = address;
        this.carNumber            = carNumber;
        this.chassisNumber        = chassisNumber;
        this.productName          = productName;
        this.premium              = premium;
        this.basePremium          = basePremium;
        this.subscriptionDate     = subscriptionDate;
        this.status               = status;
        this.occupation           = occupation;
        this.age                  = age;
        this.coveragesDescription = coveragesDescription;
        this.rejectReason         = rejectReason;
        this.supplementDocuments  = supplementDocuments;
    }
}
