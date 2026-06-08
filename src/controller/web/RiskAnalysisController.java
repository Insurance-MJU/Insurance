package controller.web;

import domain.*;
import domain.common.Money;
import infra.external.credit.CreditInquiryService;
import infra.external.credit.dto.CreditInquiryRequest;
import infra.external.credit.dto.CreditInquiryResponse;
import infra.web.Router;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RiskAnalysisController {

    private final SubscriptionList subscriptionList;
    private final RiskAnalysisReportList riskReportList;
    private final CreditInquiryService creditService;

    public RiskAnalysisController(SubscriptionList subscriptionList,
                                  RiskAnalysisReportList riskReportList,
                                  CreditInquiryService creditService) {
        this.subscriptionList = subscriptionList;
        this.riskReportList   = riskReportList;
        this.creditService    = creditService;
    }

    public void registerRoutes(Router router) {
        router.post("/subscriptions/{no}/risk-analysis",
                (req, res) -> res.ok(analyze(req.pathVariable("no"))));
    }

    private Map<String, Object> analyze(String subscriptionNo) {
        Subscription sub = subscriptionList.getByNo(subscriptionNo);

        CreditInquiryResponse resp = creditService.inquire(
                new CreditInquiryRequest(sub.getSsn(), sub.getCarNumber()));

        RiskAnalysisReport report;
        CreditInfo creditInfo = null;

        if (resp == null) {
            report = RiskAnalysisReport.defaultForNewApplicant(
                    sub.getSubscriptionNo(), sub.getBasePremium());
        } else {
            creditInfo = toCreditInfo(resp);
            report = RiskAnalysisReport.analyze(
                    sub.getSubscriptionNo(), sub.getBasePremium(), creditInfo);
        }
        riskReportList.save(report);

        return Map.of(
                "subscriptionNo",  report.getSubscriptionNo(),
                "riskScore",       report.getRiskScore(),
                "riskGrade",       report.getRiskGrade(),
                "riskGradeLabel",  report.getRiskGradeLabel(),
                "surchargeRate",   report.getSurchargeRate(),
                "surchargeAmount", report.getSurchargeAmount() != null ? report.getSurchargeAmount().getAmount() : 0L,
                "creditInfo",      creditInfo != null ? buildCreditInfo(creditInfo) : Map.of("newApplicant", true)
        );
    }

    private Map<String, Object> buildCreditInfo(CreditInfo c) {
        return Map.of(
                "accidentCount",           c.getAccidentCount(),
                "drivingExperienceYears",  c.getDrivingExperienceYears(),
                "creditGrade",             c.getCreditGrade(),
                "fraudHistory",            c.getFraudHistory(),
                "accidentHistory",         c.getAccidentHistory() != null
                        ? c.getAccidentHistory().stream().map(a -> Map.of(
                                "date",        a.getDateDisplay(),
                                "description", a.getDescription(),
                                "amount",      a.getAmount().getAmount()))
                          .collect(Collectors.toList())
                        : List.of()
        );
    }

    private CreditInfo toCreditInfo(CreditInquiryResponse r) {
        CreditInfo info = new CreditInfo();
        info.setApplicantName(r.applicantName());
        info.setCreditGrade(r.creditGrade());
        info.setDrivingExperienceYears(r.drivingExperienceYears());
        info.setFraudHistory(r.fraudHistory());
        if (r.accidentHistory() != null) {
            info.setAccidentHistory(r.accidentHistory().stream()
                    .map(a -> new CreditInfo.AccidentRecord(
                            a.date(), a.description(),
                            new Money(a.amountKrw(), "KRW")))
                    .collect(Collectors.toList()));
        }
        return info;
    }
}
