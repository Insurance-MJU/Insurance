package ui.employee;

import domain.product.Product;
import domain.product.ProductDocument;
import infra.Context;
import infra.external.FssClient;
import infra.repository.ProductRepository;
import infra.util.DocumentUploadHelper;
import java.util.*;

public class CT04ProductApproval {
    private final Scanner sc = Context.getInstance().scanner();
    private final ProductRepository productRepo = new ProductRepository();
    private final FssClient fssClient = new FssClient();

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CT-04: 상품인가를 신청한다");
        System.out.println("========================================");
        System.out.println("[상품관리 > 상품목록]");

        // ── Step 2~3: 상품 선택 ───────────────────────────────
        Product product = selectProduct();
        if (product == null) { returnToMenu(); return; }

        // ── Step 4: 상품 상세 정보 ────────────────────────────
        CT03DocumentRegistration.showProductDetail(product);

        // ── Step 5: [인가 관리] 진입 ─────────────────────────
        System.out.print("\n[인가 관리] (Enter): ");
        sc.nextLine();

        // ── Step 6: 인허가 현황 + 기초서류 목록 ──────────────
        System.out.println("\n── 인허가 진행 현황 ────────────────────");
        System.out.printf(" 현재 상태 : %s%n", product.getStatusLabel());
        CT03DocumentRegistration.printDocList(product);
        System.out.println("\n[서류등록] [요율 검증] [인가 신청] [인가 완료]");

        // ── Step 7: [요율 검증] → <<include>> CT-05 ──────────
        System.out.print("\n[요율 검증] (Enter): ");
        sc.nextLine();
        boolean rateVerified = new CT05RateVerification().runAsInclude(product);
        if (!rateVerified) { returnToMenu(); return; }

        // ── Step 8: 인가신청서 등록 ──────────────────────────
        System.out.println("\n── 보험상품 인가신청서 등록 ────────────");
        String appPath = DocumentUploadHelper.inputFilePath(sc, "인가신청서");
        if (appPath == null) { returnToMenu(); return; }

        System.out.print("\n[저장] (Enter): ");
        sc.nextLine();

        product.addDocument(ProductDocument.createSubmitted(
            product.getProductId(), ProductDocument.DocType.APPROVAL_APPLICATION,
            "보험상품 인가신청서", appPath));

        // ── Step 9: [인가 신청] 클릭 ─────────────────────────
        System.out.print("\n[인가 신청] (Enter): ");
        sc.nextLine();

        // E1: 보험개발원 확인서(요율확인서) 누락 검사
        boolean hasRateVerification = product.getDocuments().stream()
                .anyMatch(d -> d.getDocType() == ProductDocument.DocType.RATE_VERIFICATION);
        if (!hasRateVerification) {
            System.out.println("[경고] 보험개발원의 확인서가 업로드되지 않았습니다. 해당 서류는 인가 요청의 필수 항목입니다.");
            returnToMenu(); return;
        }

        // E2: 금융감독원 서류 제출
        if (!fssClient.submitApprovalApplication(product.getProductId())) {
            System.out.println("[오류] 금융감독원으로 서류 제출을 실패했습니다. 다시 시도해주세요.");
            returnToMenu(); return;
        }

        // ── Step 10: 신청 완료 ────────────────────────────────
        product.applyForApproval();
        productRepo.save(product);
        System.out.println("\n[안내] 인가 신청이 완료되었습니다. 15일 이내에 확인 결과가 통보됩니다.");

        // ── Step 11: [인가 완료] 클릭 ────────────────────────
        System.out.println("\n[안내] 금융감독원 심사 결과를 확인하고 [인가 완료] 버튼을 클릭하십시오.");
        System.out.print("[인가 완료] (Enter): ");
        sc.nextLine();

        // A1/A2: 심사 결과 (mock - 승인)
        System.out.println("\n[금융감독원 심사 결과: 승인]");
        product.completeApproval();
        productRepo.save(product);

        // ── Step 12: 인가 완료 팝업 ──────────────────────────
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│  해당 상품의 인가가 최종 승인되었습니다. │");
        System.out.println("└────────────────────────────────────────┘");
        returnToMenu();
    }

    private Product selectProduct() {
        List<Product> products = productRepo.findAll();
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
