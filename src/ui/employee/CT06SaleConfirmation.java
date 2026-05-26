package ui.employee;

import domain.*;
import infra.Context;
import infra.external.FssClient;
import infra.util.DocumentUploadHelper;
import java.util.*;

public class CT06SaleConfirmation {
    private final Scanner sc = Context.getInstance().scanner();
    private final FssClient fssClient = new FssClient();
    private final ProductList productList;

    public CT06SaleConfirmation(ProductList productList) {
        this.productList = productList;
    }

    private static final String[] REQUIRED_DOCS = {"상품 신고서", "수익성 분석 보고서", "공시자료"};
    private static final ProductDocument.DocType[] DOC_TYPES = {
        ProductDocument.DocType.SALE_NOTIFICATION,
        ProductDocument.DocType.PROFITABILITY_REPORT,
        ProductDocument.DocType.DISCLOSURE
    };

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CT-06: 상품판매를 확정한다");
        System.out.println("========================================");
        System.out.println("[상품관리 > 상품목록]");

        // ── Step 2~3: 상품 선택 ───────────────────────────────
        Product product = selectProduct();
        if (product == null) { returnToMenu(); return; }

        // ── Step 4: 상품 상세 정보 ────────────────────────────
        CT03DocumentRegistration.showProductDetail(product);

        // ── Step 5: [판매관리] 클릭 ───────────────────────────
        System.out.print("\n[판매관리] (Enter): ");
        sc.nextLine();

        // ── Step 6: 판매 현황 출력 ────────────────────────────
        System.out.println("\n── 판매 현황 ───────────────────────────");
        System.out.printf(" 현재 상태 : %s%n", product.getStatusLabel());

        ProductStatus status = product.getStatus();

        // 인가 완료(APPROVED) 상태만 판매신청 가능
        if (status == ProductStatus.APPROVED) {
            System.out.println("\n[판매신청] [판매중단]");
            runSaleApplication(product);

        // 판매 개시 전(SALE_PENDING) 상태만 판매개시 가능
        } else if (status == ProductStatus.SALE_PENDING) {
            System.out.println("\n[판매개시] [판매중단]");
            runSaleStart(product);

        } else {
            System.out.println("\n[안내] 현재 상태(" + product.getStatusLabel() + ")에서는 판매 확정을 진행할 수 없습니다.");
            System.out.println("       상품 인가(CT-04) 완료 후 다시 시도하십시오.");
        }

        returnToMenu();
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

    // ── 판매신청: APPROVED → SALE_PENDING ────────────────────
    private void runSaleApplication(Product product) {
        System.out.print("\n[판매신청]을 진행하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 판매신청이 취소되었습니다.");
            return;
        }

        // 문서 업로드
        System.out.println("\n── 판매 신청 서류 업로드 ─────────────────");
        List<ProductDocument> docs = new ArrayList<>();
        for (int i = 0; i < REQUIRED_DOCS.length; i++) {
            System.out.printf("[%d/%d] %s%n", i + 1, REQUIRED_DOCS.length, REQUIRED_DOCS[i]);
            String path = DocumentUploadHelper.inputFilePath(sc, REQUIRED_DOCS[i]);
            if (path != null) {
                docs.add(ProductDocument.createSubmitted(
                    product.getProductId(), DOC_TYPES[i], REQUIRED_DOCS[i], path));
            }
        }
        product.addDocuments(docs);
        productList.save(product);

        // FSS 신고
        System.out.println("\n── 금융감독원 판매 신고 ─────────────────");
        boolean submitted = fssClient.submitSaleNotification(product.getProductId());
        if (!submitted) {
            System.out.println("[오류] FSS 판매 신고 제출에 실패했습니다.");
            return;
        }
        System.out.println("[안내] FSS 판매 신고가 접수되었습니다. 심사 결과를 확인합니다...");

        FssClient.ReviewResult result = fssClient.getSaleReviewResult(product.getProductId());
        System.out.printf(" FSS 심사 결과: %s%n", result.getLabel());

        if (result == FssClient.ReviewResult.APPROVED) {
            product.applySalePermit();
            productList.save(product);
            System.out.println("\n[완료] 판매신청이 승인되었습니다. 상태: " + product.getStatusLabel());
            System.out.println("       [판매개시] 메뉴(CT-06)를 통해 판매를 개시할 수 있습니다.");
        } else {
            System.out.println("[안내] FSS 심사 결과: " + result.getLabel() + " — 판매신청이 반려되었습니다.");
        }
    }

    // ── 판매개시: SALE_PENDING → ON_SALE ─────────────────────
    private void runSaleStart(Product product) {
        System.out.print("\n[판매개시]를 진행하시겠습니까? (Y/N): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] 판매개시가 취소되었습니다.");
            return;
        }

        product.onsale();
        productList.save(product);
        System.out.println("\n[완료] 상품이 판매 개시되었습니다. 상태: " + product.getStatusLabel());
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
