package ui.employee;

import domain.Product;
import domain.ProductDocument;
import infra.Context;
import infra.external.FssClient;
import infra.util.DocumentUploadHelper;
import java.util.*;

public class CT06SaleConfirmation {
    private final Scanner sc = Context.getInstance().scanner();
    private final FssClient fssClient = new FssClient();

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
        System.out.println("\n[판매신청] [판매개시] [판매중단]");

        // ── Step 7: [판매신청] 클릭 ───────────────────────────
        System.out.print("\n[판매신청] (Enter): ");
        sc.nextLine();

        // ── Step 8: 서류 업로드 화면 ─────────────────────────
        System.out.println("\n── 판매 확정 신청 서류 업로드 ──────────");
        System.out.println(" (필수 서류를 모두 업로드하여야 합니다.)");

        List<ProductDocument> uploadedDocs = new ArrayList<>();
        for (ProductDocument.DocType type : DOC_TYPES) {
            System.out.printf("%n [%s]%n", type.getLabel());
            // E1: 필수 서류 누락 검사 — null 반환 시 재시도 강제
            String path;
            while (true) {
                path = DocumentUploadHelper.inputFilePath(sc, type.getLabel());
                if (path != null) break;
                System.out.println("   [경고] 필수 서류입니다. 파일 경로를 입력해야 합니다.");
            }
            uploadedDocs.add(ProductDocument.create(product.getProductId(), type, type.getLabel(), path));
        }

        // ── Step 9: [금융감독원 제출] 클릭 ───────────────────
        System.out.print("\n[금융감독원 제출] (Enter): ");
        sc.nextLine();

        // E2: 금융감독원 서류 전송 실패 (mock - 항상 성공)
        if (!fssClient.submitSaleNotification(product.getProductId())) {
            System.out.println("[오류] 금융감독원으로 서류 제출을 실패했습니다. 다시 시도해주세요.");
            returnToMenu(); return;
        }

        // ── Step 10: 제출 완료 ────────────────────────────────
        product.addDocuments(uploadedDocs);
        product.applySalePermit();
        Product.save(product);
        System.out.println("\n[안내] 금융감독원으로 서류를 제출하였습니다.");

        // ── Step 11: [판매개시] 클릭 ─────────────────────────
        System.out.println("\n[안내] 금융감독원으로부터 판매 승인 후 [판매개시] 버튼을 누르십시오.");
        System.out.print("[판매개시] (Enter): ");
        sc.nextLine();

        FssClient.ReviewResult result = fssClient.getSaleReviewResult(product.getProductId());
        System.out.println("\n[금융감독원 판매 확정 결과: " + result.getLabel() + "]");

        switch (result) {
            case APPROVED:
                // Basic Flow: 판매 승인
                product.onsale();
                Product.save(product);
                System.out.println("\n┌────────────────────────────────────────────┐");
                System.out.println("│  상품 판매가 확정되었습니다. 판매를 개시합니다. │");
                System.out.println("└────────────────────────────────────────────┘");
                break;
            case REJECTED:
                // A1: 판매 거절 → 인가완료 상태로 복귀
                product.rejectSale();
                Product.save(product);
                System.out.println("[안내] 판매 신청이 거절되었습니다. 내용 검토 후 재신청하십시오.");
                break;
            case SUPPLEMENT_REQUIRED:
                // A2: 보완 요청
                System.out.println("[안내] 서류 보완이 요청되었습니다. 보완 후 다시 제출하십시오.");
                break;
        }
        returnToMenu();
    }

    private Product selectProduct() {
        List<Product> products = Product.findAll();
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
