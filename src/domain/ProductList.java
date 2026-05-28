package domain;

import common.exception.domain.NotFoundException;
import infra.dao.ProductDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductList {
    private final ProductDao dao;
    private final List<Product> products;

    public ProductList(ProductDao dao) {
        this.dao = dao;
        this.products = Collections.emptyList();
    }

    public ProductList(List<Product> products) {
        this.dao = null;
        this.products = Collections.unmodifiableList(products);
    }

    // ── DAO 위임 ──────────────────────────────────────────────
    public ProductList findAll() {
        return dao.findAll();
    }

    // ── 도메인 로직 ────────────────────────────────────────────
    public List<Product> getAll() { return products; }
    public boolean isEmpty() { return products.isEmpty(); }
    public int size() { return products.size(); }
    public Product get(int index) { return products.get(index); }

    public ProductList onSaleOnly() {
        return new ProductList(
            products.stream()
                .filter(Product::isOnSale)
                .collect(Collectors.toList())
        );
    }

    public Product findById(String productId) {
        if (!products.isEmpty()) {
            return products.stream()
                .filter(p -> productId.equals(p.getProductId()))
                .findFirst()
                .orElse(null);
        }
        return dao.findById(productId);
    }

    public Product getById(String productId) {
        Product p = findById(productId);
        if (p == null) throw new NotFoundException("상품을 찾을 수 없습니다: " + productId);
        return p;
    }

    public boolean existsByCode(String code) {
        return dao.existsByCode(code);
    }

    public boolean existsById(String productId) {
        return findById(productId) != null;
    }

    public void validateExists(String productId) {
        if (!existsById(productId)) throw new NotFoundException("상품을 찾을 수 없습니다: " + productId);
    }

    public void save(Product product) {
        dao.save(product);
    }
}
