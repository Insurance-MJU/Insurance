package controller.cli;

import domain.AccidentList;
import domain.ClaimList;
import domain.ContractList;
import domain.CoverageList;
import domain.DamageInvestigationList;
import domain.FieldInvestigatorList;
import domain.ProductList;
import domain.RiderList;
import domain.RiskAnalysisReportList;
import domain.SubscriptionList;
import domain.common.UserRole;
import common.exception.domain.ValidationException;
import controller.cli.customer.*;
import controller.cli.employee.*;
import java.util.Scanner;

public class MainMenuController {
    private final Scanner sc = Context.getInstance().scanner();

    private final ProductList productList;
    private final SubscriptionList subscriptionList;
    private final ContractList contractList;
    private final ClaimList claimList;
    private final AccidentList accidentList;
    private final FieldInvestigatorList fieldInvestigatorList;
    private final RiderList riderList;
    private final RiskAnalysisReportList riskReportList;
    private final DamageInvestigationList damageInvestigationList;
    private final CoverageList coverageList;

    public MainMenuController(
        ProductList productList,
        SubscriptionList subscriptionList,
        ContractList contractList,
        ClaimList claimList,
        AccidentList accidentList,
        FieldInvestigatorList fieldInvestigatorList,
        RiderList riderList,
        RiskAnalysisReportList riskReportList,
        DamageInvestigationList damageInvestigationList,
        CoverageList coverageList
    ) {
        this.productList = productList;
        this.subscriptionList = subscriptionList;
        this.contractList = contractList;
        this.claimList = claimList;
        this.accidentList = accidentList;
        this.fieldInvestigatorList = fieldInvestigatorList;
        this.riderList = riderList;
        this.riskReportList = riskReportList;
        this.damageInvestigationList = damageInvestigationList;
        this.coverageList = coverageList;
    }

    public void run() {
        while (Context.getInstance().isLoggedIn()) {
            printMenu();
            String input = sc.nextLine().trim();
            if ("0".equals(input)) {
                Context.getInstance().logout();
                System.out.println("\n로그아웃되었습니다.\n");
            } else {
                dispatch(input);
            }
        }
    }

    private void printMenu() {
        UserRole role = Context.getInstance().getCurrentUser().getRole();
        System.out.println("========================================");
        System.out.println(" [메인 메뉴] (" + label(role) + ")");
        System.out.println("========================================");

        if (role == UserRole.CUSTOMER) {
            System.out.println(" 1. 상품가입을 요청한다     (CS-01)");
            System.out.println(" 2. 보험상품을 조회한다     (CS-02)");
            System.out.println(" 3. 예상보험료를 산출한다   (CS-03)");
            System.out.println(" 4. 보험금을 청구한다       (CS-04)");
            System.out.println(" 5. 보험계약을 조회한다     (CS-05)");
        } else {
            System.out.println(" ── 상품 관리 ──────────────────");
            System.out.println("  1. 상품을 설계한다         (CT-01)");
            System.out.println("  2. 보험료를 산출한다       (CT-02)");
            System.out.println("  3. 기초서류를 등록한다     (CT-03)");
            System.out.println("  4. 상품인가를 신청한다     (CT-04)");
            System.out.println("  5. 요율검증을 요청한다     (CT-05)");
            System.out.println("  6. 상품판매를 확정한다     (CT-06)");
            System.out.println(" ── 계약 인수 ──────────────────");
            System.out.println("  7. 계약인수를 심사한다     (UW-01)");
            System.out.println("  8. 위험성을 분석한다       (UW-02)");
            System.out.println(" ── 보상 관리 ───────────────────");
            System.out.println("  9. 신규 사고 접수 내역     (CL-01)");
            System.out.println(" 10. 손해액 산정             (CL-02)");
            System.out.println(" 11. 손해를 조사한다         (CL-03)");
            System.out.println(" 12. 보험금을 지급한다       (CL-04)");
        }

        System.out.println(" 0. 로그아웃");
        System.out.print("선택: ");
    }

    private void dispatch(String input) {
        try {
            doDispatch(input);
        } catch (ValidationException e) {
            System.out.println();
            e.getErrors().forEach(err -> System.out.println("[입력 오류] " + err));
            System.out.println();
        }
    }

    private void doDispatch(String input) {
        UserRole role = Context.getInstance().getCurrentUser().getRole();

        if (role == UserRole.CUSTOMER) {
            switch (input) {
                case "1": new CS01ProductSubscription(productList, subscriptionList, riderList).run();             break;
                case "2": new CS02ProductInquiry(productList, riderList).run();                                    break;
                case "3": new CS03PremiumEstimate(productList, riderList).run();                                   break;
                case "4": new CS04ClaimRequest(accidentList, contractList, subscriptionList).run();                break;
                case "5": new CS05ContractInquiry(subscriptionList, contractList).run();                           break;
                default:  invalid(); break;
            }
        } else {
            switch (input) {
                case "1":  new CT01ProductDesign(riderList, productList, coverageList).run();                      break;
                case "2":  new CT02PremiumCalculation(productList).run();                                          break;
                case "3":  new CT03DocumentRegistration(productList).run();                                        break;
                case "4":  new CT04ProductApproval(productList).run();                                             break;
                case "5":  new CT05RateVerification(productList).run();                                            break;
                case "6":  new CT06SaleConfirmation(productList).run();                                            break;
                case "7":  new UW01ContractReview(subscriptionList, contractList, riskReportList).run();           break;
                case "8":  new UW02RiskAnalysis(riskReportList).run();                                             break;
                case "9":  new CL01AccidentRegistration(accidentList, claimList, fieldInvestigatorList).run();     break;
                case "10": new CL02DamageAssessment(claimList, accidentList, contractList, damageInvestigationList).run(); break;
                case "11": new CL03DamageInvestigation(accidentList, claimList, damageInvestigationList).run();    break;
                case "12": new CL04InsurancePayment(claimList, accidentList).run();                                break;
                default:   invalid(); break;
            }
        }
    }

    private void invalid() {
        System.out.println("[오류] 올바른 메뉴를 선택해주세요.\n");
    }

    private String label(UserRole role) {
        if (role == UserRole.CUSTOMER) return "고객";
        if (role == UserRole.EMPLOYEE) return "직원";
        return "관리자";
    }
}
