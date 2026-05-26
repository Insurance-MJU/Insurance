package infra;

import domain.*;
import infra.config.DBConfig;
import infra.dao.*;
import infra.persistence.Database;
import ui.LoginController;
import ui.MainMenuController;

public class AppContext {
    private final LoginController loginController;
    private final MainMenuController mainMenuController;

    private AppContext(LoginController loginController, MainMenuController mainMenuController) {
        this.loginController = loginController;
        this.mainMenuController = mainMenuController;
    }

    public static AppContext initialize() {
        // ── 1. 인프라 (Database) ────────────────────────────────
        Database db = new Database(DBConfig.defaults());

        // ── 2. DAO (Database 주입) ──────────────────────────────
        UserDao               userDao               = new UserDao(db);
        AccidentDao           accidentDao           = new AccidentDao(db);
        ClaimDao              claimDao              = new ClaimDao(db);
        ContractDao           contractDao           = new ContractDao(db);
        SubscriptionDao       subscriptionDao       = new SubscriptionDao(db);
        ProductDao            productDao            = new ProductDao(db);
        RiderDao              riderDao              = new RiderDao(db);
        EmployeeDao           employeeDao           = new EmployeeDao(db);
        RiskAnalysisReportDao riskReportDao         = new RiskAnalysisReportDao(db);
        DamageInvestigationDao damageInvDao         = new DamageInvestigationDao(db);
        CoverageDao           coverageDao           = new CoverageDao(db);

        // ── 3. 도메인 컬렉션 (DAO 주입) ─────────────────────────
        AccidentList           accidentList         = new AccidentList(accidentDao);
        ClaimList              claimList            = new ClaimList(claimDao);
        ContractList           contractList         = new ContractList(contractDao);
        SubscriptionList       subscriptionList     = new SubscriptionList(subscriptionDao);
        ProductList            productList          = new ProductList(productDao);
        RiderList              riderList            = new RiderList(riderDao);
        FieldInvestigatorList  fieldInvestigatorList = new FieldInvestigatorList(employeeDao);
        RiskAnalysisReportList riskReportList       = new RiskAnalysisReportList(riskReportDao);
        DamageInvestigationList damageInvList       = new DamageInvestigationList(damageInvDao);
        CoverageList           coverageList         = new CoverageList(coverageDao);

        // ── 4. 컨트롤러 조립 (도메인 모델 주입) ────────────────
        LoginController loginController = new LoginController(userDao);

        MainMenuController mainMenuController = new MainMenuController(
            productList, subscriptionList, contractList, claimList,
            accidentList, fieldInvestigatorList, riderList,
            riskReportList, damageInvList, coverageList
        );

        return new AppContext(loginController, mainMenuController);
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public MainMenuController getMainMenuController() {
        return mainMenuController;
    }
}
