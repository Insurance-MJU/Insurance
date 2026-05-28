package controller.web.dto;

import domain.Product;
import java.text.SimpleDateFormat;

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
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getProductId(),
                p.getProductCode(),
                p.getProductName(),
                p.getStatusLabel(),
                p.getTargetDescription(),
                p.getSaleStartDate() != null ? SDF.format(p.getSaleStartDate()) : null,
                p.getSaleEndDate()   != null ? SDF.format(p.getSaleEndDate())   : null,
                p.getDescription()
        );
    }
}
