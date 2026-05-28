package controller.cli.employee;

import domain.*;
import infra.Context;
import common.util.DocumentUploadHelper;
import java.util.*;

public class CT03DocumentRegistration {
    private final Scanner sc = Context.getInstance().scanner();
    private final ProductList productList;

    public CT03DocumentRegistration(ProductList productList) {
        this.productList = productList;
    }

    private static final String[] DOC_NAMES = {"사업방법서", "보험약관", "산출방법서"};
    private static final ProductDocument.DocType[] DOC_TYPES = {
        ProductDocument.DocType.BASIC_DOCUMENT,
        ProductDocument.DocType.GENERAL_TERMS,
        ProductDocument.DocType.BASIC_DOCUMENT
    };

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CT-03: 기초서류를 등록한다");
        System.out.println("========================================");
        System.out.println("[상품관리 > 상품목록]");

        Product product = selectProduct();
        if (product == null) { returnToMenu(); return; }

        showProductDetail(product);

        System.out.print("\n[인가 관리] (Enter): ");
        sc.nextLine();

        System.out.println("\n── 인허가 진행 현황 ────────────────────");
        System.out.printf(" 현재 상태 : %s%n", product.getStatusLabel());
        printDocList(product);
        System.out.println("\n[서류등록] [요율 검증] [인가 신청] [인가 완료]");

        System.out.print("\n[서류등록] (Enter): ");
        sc.nextLine();

        // ── Step 8~9: 파일 경로 입력 및 검증 ─────────────────
        System.out.println("\n── 기초서류 업로드 ─────────────────────");
        System.out.println(" 지원 형식: pdf, docx, xlsx, hwpx");

        List<ProductDocument> newDocs = new ArrayList<>();
        for (int i = 0; i < DOC_NAMES.length; i++) {
            System.out.printf("%n [%d] %s%n", i + 1, DOC_NAMES[i]);
            String path = DocumentUploadHelper.inputFilePath(sc, DOC_NAMES[i]);
            if (path == null) {
                System.out.println("   → 건너뜀");
                continue;
            }
            newDocs.add(ProductDocument.create(product.getProductId(), DOC_TYPES[i], DOC_NAMES[i], path));
        }

        System.out.print("\n[저장] (Enter): ");
        sc.nextLine();

        product.addDocuments(newDocs);

        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.println("│  파일이 성공적으로 저장되었습니다.    │");
        System.out.println("└──────────────────────────────────────┘");
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

    static void showProductDetail(Product p) {
        System.out.println("\n── 상품 상세 정보 ──────────────────────");
        System.out.printf(" 상품명    : %s%n", p.getProductName());
        System.out.printf(" 보험종목  : %s%n", p.getLobLabel());
        if (p.getSaleStartDate() != null) System.out.printf(" 판매시작일: %tF%n", p.getSaleStartDate());
        if (p.getSaleEndDate()   != null) System.out.printf(" 판매종료일: %tF%n", p.getSaleEndDate());
        System.out.printf(" 가입대상  : %s%n", p.getTargetDescription());
        System.out.printf(" 현재상태  : %s%n", p.getStatusLabel());
        System.out.println("\n[수정] [인가 관리] [판매관리]");
    }

    static void printDocList(Product p) {
        System.out.println("\n[기초서류 목록]");
        List<ProductDocument> docs = p.getDocuments();
        if (docs == null || docs.isEmpty()) {
            System.out.println(" (등록된 문서 없음)");
        } else {
            for (ProductDocument d : docs) {
                System.out.printf(" - [%s] %s%n   경로: %s%n", d.getDocType(), d.getTitle(), d.getNote());
            }
        }
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
