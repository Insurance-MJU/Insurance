package controller.web.dto;

import common.util.DateUtil;
import domain.Product;

public record ProductResponse(
        String productId,
        String productCode,
        String productName,
        String status,
        String target,
        String saleStartDate,
        String saleEndDate,
        String description
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getProductId(),
                p.getProductCode(),
                p.getProductName(),
                p.getStatusLabel(),
                p.getTargetDescription(),
                DateUtil.format(p.getSaleStartDate()),
                DateUtil.format(p.getSaleEndDate()),
                p.getDescription()
        );
    }
}
