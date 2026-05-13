package ui.customer;

import domain.*;
import infra.Context;
import infra.repository.ProductRepository;
import infra.repository.RiderRepository;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class CS02ProductInquiry {

    private final Scanner sc = Context.getInstance().scanner();
    private final ProductRepository productRepo = new ProductRepository();
    private final RiderRepository riderRepo = new RiderRepository();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 상품 조회 및 선택.
     * 메인 메뉴에서 단독 실행 시 반환값은 무시됨.
     * CS-01에서 include 시 선택된 Product를 반환하며, 취소하면 null 반환.
     */
    public Product run() {
        List<Product> products = productRepo.findAll();

        while (true) {
            // Step 1: 판매 중인 상품 목록 출력
            System.out.println("\n========================================");
            System.out.println(" CS-02: 보험상품을 조회한다");
            System.out.println("========================================");
            System.out.println("\n[판매 상품 목록]");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                System.out.printf(" %d. %-35s [%s]%n",
                        i + 1, p.getProductName(), p.getStatusLabel());
            }
            System.out.println(" 0. 뒤로가기");
            System.out.print("선택: ");

            String choice = sc.nextLine().trim();
            if ("0".equals(choice)) return null;

            int idx;
            try {
                idx = Integer.parseInt(choice) - 1;
                if (idx < 0 || idx >= products.size()) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("[오류] 올바른 번호를 입력해주세요.");
                continue;
            }

            // Step 2: 사용자가 상품 선택
            Product selected = products.get(idx);
            Product result = showProductDetail(selected);
            if (result != null) return result; // [가입하기] 선택됨
            // result == null → 뒤로가기 → 목록으로 돌아감
        }
    }

    // Step 3 ~ 9: 상품 상세 화면
    private Product showProductDetail(Product p) {
        while (true) {
            printProductDetail(p);

            System.out.println("\n선택:");
            List<ProductRider> riders = p.getRiders();
            for (int i = 0; i < riders.size(); i++) {
                System.out.printf("  r%d - %s 상세보기%n", i + 1, riders.get(i).getRiderName());
            }
            System.out.println("  t  - 약관보기");
            System.out.println("  s  - 가입하기");
            System.out.println("  b  - 뒤로가기");
            System.out.print("입력: ");

            String input = sc.nextLine().trim().toLowerCase();

            if ("b".equals(input)) return null;

            if ("t".equals(input)) {
                showTerms(p);
                continue;
            }

            if ("s".equals(input)) {
                // E1: 판매 중지 상태 확인
                if (!p.isOnSale()) {
                    System.out.println("\n[안내] 해당 상품은 현재 판매가 중지된 상태입니다.");
                    return null; // 목록으로 돌아가기
                }
                // Step 9
                System.out.println("\n보험 가입 화면으로 이동합니다.");
                return p;
            }

            // 특약 상세 조회 (r1, r2 ...)
            if (input.startsWith("r") && input.length() > 1) {
                try {
                    int rIdx = Integer.parseInt(input.substring(1)) - 1;
                    if (rIdx >= 0 && rIdx < riders.size()) {
                        showRiderDetail(riders.get(rIdx));
                        continue;
                    }
                } catch (NumberFormatException ignored) {}
            }

            System.out.println("[오류] 올바른 선택지를 입력해주세요.");
        }
    }

    // Step 3: 상품 상세 정보 출력
    private void printProductDetail(Product p) {
        System.out.println("\n----------------------------------------");
        System.out.println("[상품 상세 정보]");
        System.out.printf(" 상품명      : %s%n", p.getProductName());
        System.out.printf(" 상품코드    : %s%n", p.getProductCode());
        System.out.printf(" 판매기간    : %s ~ %s%n",
                p.getSaleStartDate() != null ? sdf.format(p.getSaleStartDate()) : "-",
                p.getSaleEndDate()   != null ? sdf.format(p.getSaleEndDate())   : "-");
        System.out.printf(" 가입대상    : %s%n", p.getTargetDescription());
        System.out.printf(" 판매상태    : %s%n", p.getStatusLabel());

        List<ProductRider> riders = p.getRiders();
        if (riders != null && !riders.isEmpty()) {
            System.out.println("\n [특약 목록]");
            for (int i = 0; i < riders.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, riders.get(i).getRiderName());
            }
        }
        System.out.println("----------------------------------------");
    }

    // Step 4-5: 특약 상세
    private void showRiderDetail(ProductRider pr) {
        Rider master = riderRepo.findByCode(pr.getRiderCode());
        System.out.println("\n[특약 상세 정보]");
        System.out.printf(" 제목 : %s%n", pr.getRiderName());
        if (master != null) {
            System.out.println(" 설명 :");
            for (String line : master.getDescription().split("\n")) {
                System.out.println("   " + line);
            }
        }
        System.out.print("\nEnter를 눌러 돌아갑니다...");
        sc.nextLine();
    }

    // Step 6-7: 약관 보기
    private void showTerms(Product p) {
        List<ProductDocument> docs = p.getDocuments();
        if (docs == null || docs.isEmpty()) {
            System.out.println("\n[안내] 등록된 약관이 없습니다.");
            System.out.print("Enter를 눌러 돌아갑니다...");
            sc.nextLine();
            return;
        }

        System.out.println("\n[약관 목록]");
        for (int i = 0; i < docs.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, docs.get(i).getTitle());
        }
        System.out.print("조회할 약관 번호 (0: 취소): ");
        String input = sc.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
            if (idx < 0) return;
            if (idx >= docs.size()) { System.out.println("[오류] 잘못된 번호입니다."); return; }
        } catch (NumberFormatException e) { return; }

        ProductDocument doc = docs.get(idx);
        System.out.println("\n======== " + doc.getTitle() + " ========");
        System.out.println(doc.getNote());
        System.out.println("=".repeat(40));

        // A1: PDF 저장
        System.out.print("\n문서를 저장하시겠습니까? (Y/N): ");
        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[안내] PDF 파일이 저장되었습니다.");
        }
        System.out.print("Enter를 눌러 돌아갑니다...");
        sc.nextLine();
    }
}
