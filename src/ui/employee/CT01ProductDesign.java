package ui.employee;

import domain.product.*;
import domain.provision.Coverage;
import domain.provision.CoverageLimitOption;
import domain.provision.Rider;
import infra.Context;
import infra.repository.CoverageRepository;
import infra.repository.ProductRepository;
import infra.repository.RiderRepository;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CT01ProductDesign {
    private final Scanner sc = Context.getInstance().scanner();
    private final ProductRepository  productRepo  = new ProductRepository();
    private final CoverageRepository coverageRepo = new CoverageRepository();
    private final RiderRepository    riderRepo    = new RiderRepository();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public void run() {
        System.out.println("\n========================================");
        System.out.println(" CT-01: 상품을 설계한다");
        System.out.println("========================================");
        System.out.println("[상품관리 > 신규 상품 등록]");

        // ── Step 2~3: 상품 기본정보 입력 ─────────────────────────
        System.out.println("\n── 신규 상품 등록 폼 ──────────────────");
        System.out.print(" 상품명: ");
        String productName = sc.nextLine().trim();

        // E1: 상품코드 중복 검사
        String productCode;
        while (true) {
            System.out.print(" 상품코드 (예: CAR-2026-MZ): ");
            productCode = sc.nextLine().trim();
            if (productRepo.existsByCode(productCode)) {
                System.out.println("[오류] 이미 사용 중인 상품코드입니다.");
            } else {
                break;
            }
        }

        System.out.println(" 보험종목:");
        System.out.println("  1. 개인용자동차보험");
        System.out.println("  2. 업무용자동차보험");
        System.out.println("  3. 영업용자동차보험");
        System.out.print(" 선택: ");
        String lobChoice = sc.nextLine().trim();
        Product.Target target;
        if ("2".equals(lobChoice))      target = Product.Target.BUSINESS;
        else if ("3".equals(lobChoice)) target = Product.Target.COMMERCIAL;
        else                            target = Product.Target.PERSONAL;

        System.out.print(" 판매시작일 (yyyy-MM-dd): ");
        String startStr = sc.nextLine().trim();
        System.out.print(" 판매종료일 (yyyy-MM-dd): ");
        String endStr = sc.nextLine().trim();
        Date saleStart = null, saleEnd = null;
        try { saleStart = SDF.parse(startStr); } catch (Exception ignored) {}
        try { saleEnd   = SDF.parse(endStr);   } catch (Exception ignored) {}

        System.out.print(" 가입대상 (예: 만 20세 이상 39세 이하 운전자): ");
        sc.nextLine();
        System.out.print(" 설명: ");
        String description = sc.nextLine().trim();

        System.out.print("\n[다음] (Enter): ");
        sc.nextLine();

        // ── Step 4~5: 담보 선택 (CoverageRepository) ─────────────
        List<Coverage> allCoverages = coverageRepo.findAll();
        Map<Coverage, List<CoverageLimitOption>> selectedOptions;

        while (true) {
            System.out.println("\n── 담보 목록 ──────────────────────────");
            for (int i = 0; i < allCoverages.size(); i++) {
                Coverage c = allCoverages.get(i);
                System.out.printf("  %d. %s%s%n", i + 1, c.getCoverageName(),
                        c.isMandatory() ? " (필수)" : "");
            }
            System.out.print("\n선택할 담보 번호 (쉼표 구분, 예: 1,2,3): ");
            List<Integer> idxs = parseNumbers(sc.nextLine().trim(), allCoverages.size());

            // A1: 대인배상 I(필수) 포함 여부 검사
            boolean hasMandatory = idxs.stream()
                    .map(allCoverages::get)
                    .anyMatch(c -> c.getCoverageType() == Coverage.CoverageType.PERSONAL_INJURY_MANDATORY);
            if (!hasMandatory) {
                System.out.println("[경고] 대인배상 I은 자동차보험의 필수 담보입니다. 담보 리스트에 추가해 주세요.");
                continue;
            }

            // 가입 옵션 선택
            selectedOptions = new LinkedHashMap<>();
            for (int idx : idxs) {
                Coverage c = allCoverages.get(idx);
                List<CoverageLimitOption> opts = c.getLimitOptions();
                if (opts == null || opts.size() <= 1) {
                    selectedOptions.put(c, opts != null ? opts : Collections.emptyList());
                } else {
                    System.out.printf("%n[%s 가입 옵션]%n", c.getCoverageName());
                    for (int j = 0; j < opts.size(); j++) {
                        System.out.printf("  %d. %s%n", j + 1, opts.get(j).getOptionName());
                    }
                    System.out.print(" 선택 (쉼표로 복수 선택): ");
                    List<Integer> optIdxs = parseNumbers(sc.nextLine().trim(), opts.size());
                    List<CoverageLimitOption> chosen = optIdxs.stream()
                            .map(opts::get).collect(Collectors.toList());
                    selectedOptions.put(c, chosen.isEmpty() ? Collections.singletonList(opts.get(0)) : chosen);
                }
            }
            break;
        }

        System.out.print("\n[다음] (Enter): ");
        sc.nextLine();

        // ── Step 6~7: 특약 선택 (RiderRepository) ────────────────
        List<Rider> allRiders = riderRepo.findAll();
        System.out.println("\n── 특약 목록 ──────────────────────────");
        for (int i = 0; i < allRiders.size(); i++) {
            Rider r = allRiders.get(i);
            String disc = r.getDiscountRate() != null
                    ? String.format(" (%.0f%% 할인)", r.getDiscountRate() * 100) : "";
            System.out.printf("  %d. %s%s%n", i + 1, r.getRiderName(), disc);
        }
        System.out.print("선택 번호 (없으면 Enter): ");
        String riderInput = sc.nextLine().trim();
        List<Rider> selectedRiders = riderInput.isEmpty()
                ? Collections.emptyList()
                : parseNumbers(riderInput, allRiders.size()).stream()
                        .map(allRiders::get).collect(Collectors.toList());

        System.out.print("\n[다음] (Enter): ");
        sc.nextLine();

        // ── Step 8: 설계 요약 ─────────────────────────────────
        System.out.println("\n── 상품 설계 요약 ──────────────────────");
        System.out.printf(" 상품명    : %s%n", productName);
        System.out.printf(" 상품코드  : %s%n", productCode);
        System.out.printf(" 보험종목  : %s%n", target.getAutoLabel());
        System.out.printf(" 판매기간  : %s ~ %s%n", startStr, endStr);
        System.out.println(" 선택 담보:");
        for (Map.Entry<Coverage, List<CoverageLimitOption>> e : selectedOptions.entrySet()) {
            String optStr = e.getValue().stream()
                    .map(CoverageLimitOption::getOptionName)
                    .collect(Collectors.joining(", "));
            System.out.printf("   - %s [%s]%n", e.getKey().getCoverageName(),
                    optStr.isEmpty() ? "기본 옵션" : optStr);
        }
        if (!selectedRiders.isEmpty()) {
            System.out.println(" 선택 특약:");
            selectedRiders.forEach(r -> System.out.printf("   - %s%n", r.getRiderName()));
        }

        // ── Step 9: <<include>> CT-02 ─────────────────────────
        System.out.print("\n[보험료 산출] (Enter): ");
        sc.nextLine();

        List<String> selectedCoverageNames = selectedOptions.keySet().stream()
                .map(Coverage::getCoverageName).collect(Collectors.toList());
        long[] premiumResult = new CT02PremiumCalculation().runAsInclude(productName, selectedCoverageNames, saleEnd);
        if (premiumResult == null) { returnToMenu(); return; }

        long finalPremium = premiumResult[0];
        long reserve      = premiumResult[1];

        // ── Step 10: 최종 결과 ────────────────────────────────
        System.out.println("\n── 최종 산출 결과 ──────────────────────");
        System.out.printf(" 기본형 최종 보험료   : %,d원%n", finalPremium);
        System.out.printf(" 준비금 적립 필요액   : %,d원%n", reserve);

        // ── Step 11: 상품 확정 ────────────────────────────────
        System.out.print("\n[상품 확정] (Enter): ");
        sc.nextLine();

        Product product = Product.design(productCode, productName, description, target, saleStart, saleEnd);
        product.setRiders(buildProductRiders(selectedRiders));
        productRepo.save(product);

        // ── Step 12: 완료 팝업 ────────────────────────────────
        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.println("│    상품 설계가 완료되었습니다.         │");
        System.out.println("└──────────────────────────────────────┘");
        returnToMenu();
    }

    private List<ProductRider> buildProductRiders(List<Rider> riders) {
        return riders.stream()
                .map(ProductRider::from)
                .collect(Collectors.toList());
    }

    private List<Integer> parseNumbers(String input, int max) {
        List<Integer> result = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) return result;
        for (String s : input.split(",")) {
            try {
                int n = Integer.parseInt(s.trim()) - 1;
                if (n >= 0 && n < max && !result.contains(n)) result.add(n);
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    private void returnToMenu() {
        System.out.print("\nEnter를 누르면 메인 메뉴로 돌아갑니다...");
        sc.nextLine();
        System.out.println();
    }
}
