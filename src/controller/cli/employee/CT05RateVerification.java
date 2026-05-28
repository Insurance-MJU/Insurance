package controller.cli.employee;

import domain.*;
import controller.cli.Context;
import infra.external.kidi.KidiService;
import common.util.DocumentUploadHelper;
import java.util.*;

public class CT05RateVerification {
    private final Scanner sc = Context.getInstance().scanner();
    private final KidiService kidiService;
    private final ProductList productList;

    public CT05RateVerification(ProductList productList, KidiService kidiService) {
        this.productList = productList;
        this.kidiService = kidiService;
    }

    private static final String[] REQUIRED_DOCS = {"요율 산출 근거서", "담보별 기준 순보험료 산출표"};

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CT-05: 요율검증을 요청한다");
        System.out.println("========================================");

        Product product = selectProduct();
        if (product == null) { returnToMenu(); return; }

        boolean ok = runAsInclude(product);
        if (!ok) System.out.println("[안내] 요율 검증이 취소되었습니다.");
        returnToMenu();
    }

    /**
     * CT-04에 의해 include.
     * @return true: 요율확인서 등록 완료, false: 취소/오류
     */
    public boolean runAsInclude(Product product) {
        System.out.println("\n========================================");
        System.out.println(" CT-05: 요율검증을 요청한다");
        System.out.println("========================================");
        System.out.println("(CT-04에 의해 include)");

        // ── Step 1: 상태 및 제출 서류 목록 ───────────────────
        System.out.println("\n── 보험개발원 요율검증 요청 ────────────");
        System.out.printf(" 현재 상태   : %s%n", product.getStatusLabel());
        System.out.println("\n[제출 서류 목록]");
        for (String doc : REQUIRED_DOCS) System.out.printf(" - %s%n", doc);

        // ── Step 2: 서류 첨부 ────────────────────────────────
        System.out.println("\n[파일 첨부]");
        for (String docName : REQUIRED_DOCS) {
            System.out.printf("%n [%s]%n", docName);
            DocumentUploadHelper.inputFilePath(sc, docName);
        }

        System.out.print("\n[저장] (Enter): ");
        sc.nextLine();

        // ── Step 3: 첨부 완료 팝업 ───────────────────────────
        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.println("│    서류 첨부가 완료되었습니다.         │");
        System.out.println("└──────────────────────────────────────┘");

        // ── Step 4: [보험개발원 제출] 클릭 ───────────────────
        System.out.print("\n[보험개발원 제출] (Enter): ");
        sc.nextLine();

        // E1: 보험개발원 시스템 연결 실패 (mock - 항상 성공)
        if (!kidiService.submitRateVerification(product.getProductId())) {
            System.out.println("[오류] 보험개발원 시스템 연결에 실패하였습니다. 잠시 후 다시 시도해 주세요.");
            return false;
        }

        // ── Step 5: 제출 완료 ─────────────────────────────────
        System.out.println("\n[안내] 보험개발원으로 서류가 제출되었습니다. 최대 15일이 소요될 수 있습니다.");

        // ── Step 6: 요율확인서 업로드 ────────────────────────
        System.out.println("\n[안내] 보험개발원으로부터 요율확인서를 수령한 후 업로드하십시오.");
        String confirmPath = DocumentUploadHelper.inputFilePath(sc, "요율확인서");
        if (confirmPath == null) return false;

        System.out.print("\n[저장] (Enter): ");
        sc.nextLine();

        // 요율확인서 ProductDocument 저장
        product.addDocument(ProductDocument.createReceived(
            product.getProductId(), ProductDocument.DocType.RATE_VERIFICATION,
            "요율확인서", confirmPath));

        // ── Step 7: 인가신청서 등록 안내 ─────────────────────
        System.out.println("\n[안내] 요율확인서가 등록되었습니다. 인가신청서를 등록하시겠습니까?");
        System.out.print(" 1. 예  2. 아니오  → 선택: ");
        String answer = sc.nextLine().trim();
        if ("1".equals(answer)) {
            System.out.println("[안내] 인가신청서 등록화면으로 이동합니다.");
            System.out.println("(CT-04의 Basic Flow 8번으로 넘어갑니다.)");
        }

        return true;
    }

    private Product selectProduct() {
        ProductList products = productList.findAll();
        System.out.println("\n[등록된 상품 목록]");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.printf(" %d. %s (%s) [%s]%n",
                    i + 1, p.getProductName(), p.getProductCode(), p.getStatusLabel());
        }
        System.out.print("\n 상품 번호 선택 (0: 취소): ");
        try {
            int idx = Integer.parseInt(sc.nextLine().trim());
            if (idx == 0) return null;
            return products.get(idx - 1);
        } catch (Exception e) { return null; }
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
