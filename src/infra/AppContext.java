package infra;

import controller.cli.Context;
import controller.cli.LoginController;
import controller.cli.MainMenuController;
import controller.web.*;
import domain.*;
import infra.config.AppConfig;
import infra.config.JwtConfig;
import infra.dao.*;
import infra.external.bank.mock.MockBankService;
import infra.external.credit.mock.MockCreditInquiryService;
import infra.external.fss.mock.MockFssService;
import infra.external.kidi.mock.MockKidiService;
import infra.external.vehicle.mock.MockVehicleInquiryService;
import infra.external.verification.mock.MockVerificationService;
import infra.persistence.Database;
import infra.web.DispatcherServlet;
import infra.web.Router;
import infra.web.Server;
import infra.web.auth.JwtFilter;
import infra.web.auth.JwtUtil;

public class AppContext {

    private final LoginController loginController;
    private final MainMenuController mainMenuController;
    private final DispatcherServlet dispatcherServlet;
    private final Server server;

    private AppContext(LoginController loginController, MainMenuController mainMenuController,
                       DispatcherServlet dispatcherServlet, Server server) {
        this.loginController    = loginController;
        this.mainMenuController = mainMenuController;
        this.dispatcherServlet  = dispatcherServlet;
        this.server             = server;
    }

    public static AppContext initialize(AppConfig config) {
        // ── 1. 인프라 ────────────────────────────────────────────
        Database db = new Database(config.getDbConfig());

        // ── 외부 서비스 Mock ─────────────────────────────────────
        MockVehicleInquiryService vehicleService     = new MockVehicleInquiryService();
        MockVerificationService   verificationService = new MockVerificationService();
        MockBankService           bankService         = new MockBankService();
        MockCreditInquiryService  creditService       = new MockCreditInquiryService();
        MockFssService            fssService          = new MockFssService();
        MockKidiService           kidiService         = new MockKidiService();

        // ── 2. DB DAO ────────────────────────────────────────────
        UserDao                userDao           = new UserDao(db);
        AccidentDao            accidentDao       = new AccidentDao(db);
        ClaimDao               claimDao          = new ClaimDao(db);
        ContractDao            contractDao       = new ContractDao(db);
        SubscriptionDao        subscriptionDao   = new SubscriptionDao(db);
        ProductDao             productDao        = new ProductDao(db);
        RiderDao               riderDao          = new RiderDao(db);
        EmployeeDao            employeeDao       = new EmployeeDao(db);
        RiskAnalysisReportDao  riskReportDao     = new RiskAnalysisReportDao(db);
        DamageInvestigationDao damageInvDao      = new DamageInvestigationDao(db);
        CoverageDao            coverageDao       = new CoverageDao(db);

        // ── 3. 외부 서비스 DAO 래퍼 ──────────────────────────────
        CarDao          carDao          = new CarDao(vehicleService);
        VerificationDao verificationDao = new VerificationDao(verificationService);
        CreditDao       creditDao       = new CreditDao(creditService);
        BankDao         bankDao         = new BankDao(bankService);
        FssDao          fssDao          = new FssDao(fssService);
        KidiDao         kidiDao         = new KidiDao(kidiService);

        // ── 4. 도메인 컬렉션 ─────────────────────────────────────
        UserList                userList              = new UserList(userDao);
        AccidentList            accidentList          = new AccidentList(accidentDao);
        ClaimList               claimList             = new ClaimList(claimDao);
        ContractList            contractList          = new ContractList(contractDao);
        SubscriptionList        subscriptionList      = new SubscriptionList(subscriptionDao);
        ProductList             productList           = new ProductList(productDao);
        RiderList               riderList             = new RiderList(riderDao);
        FieldInvestigatorList   fieldInvestigatorList = new FieldInvestigatorList(employeeDao);
        RiskAnalysisReportList  riskReportList        = new RiskAnalysisReportList(riskReportDao);
        DamageInvestigationList damageInvList         = new DamageInvestigationList(damageInvDao);
        CoverageList            coverageList          = new CoverageList(coverageDao);

        // ── 5. 외부 서비스 도메인 모델 ───────────────────────────
        CarList               carList          = new CarList(carDao);
        IdentityVerifier      identityVerifier = new IdentityVerifier(verificationDao);
        CreditList            creditList       = new CreditList(creditDao);
        BankGateway           bankGateway      = new BankGateway(bankDao);
        ProductApprovalGateway approvalGateway = new ProductApprovalGateway(fssDao, kidiDao);

        // ── 6. CLI 컨트롤러 ──────────────────────────────────────
        LoginController loginController = new LoginController(userDao);
        MainMenuController mainMenuController = new MainMenuController(
                productList, subscriptionList, contractList, claimList,
                accidentList, fieldInvestigatorList, riderList,
                riskReportList, damageInvList, coverageList,
                carList, identityVerifier, bankGateway, creditList, approvalGateway
        );

        // ── 7. JWT / Web 인프라 ──────────────────────────────────
        JwtConfig jwtConfig = config.getJwtConfig();
        JwtUtil   jwtUtil   = new JwtUtil(jwtConfig);
        JwtFilter jwtFilter = new JwtFilter(jwtUtil);

        // ── 8. Web 컨트롤러 + 라우팅 ────────────────────────────
        Router router = new Router();
        new AuthController(userList, jwtUtil).registerRoutes(router);
        new VehicleController(vehicleService).registerRoutes(router);
        new VerificationController(verificationService).registerRoutes(router);
        new ProductController(productList, riderList).registerRoutes(router);
        new SubscriptionController(subscriptionList, productList, verificationService).registerRoutes(router);
        new AccidentController(accidentList, claimList, contractList, fieldInvestigatorList).registerRoutes(router);
        new ClaimController(claimList).registerRoutes(router);

        DispatcherServlet dispatcher = new DispatcherServlet(router, jwtFilter);
        Server server = new Server(config.getServerConfig());

        return new AppContext(loginController, mainMenuController, dispatcher, server);
    }

    // ── CLI 진입점 ───────────────────────────────────────────────
    public void startCli() {
        loginController.run();
        if (Context.getInstance().isLoggedIn()) {
            mainMenuController.run();
        }
    }

    // ── Web 진입점 ───────────────────────────────────────────────
    public void startWeb() {
        server.start(dispatcherServlet);
    }
}
