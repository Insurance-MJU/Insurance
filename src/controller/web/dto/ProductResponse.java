package controller.web.dto;

import common.util.DateUtil;
import domain.Product;
import domain.ProductDocument;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResponse(
        String id,
        String productId,
        String productCode,
        String productName,
        String status,
        String statusDisplayName,
        String target,
        String saleStartDate,
        String saleEndDate,
        String description,
        List<DocInfo> documents
) {
    public record DocInfo(String id, String docType, String title, String note, String filename,
                          String submittedAt, String receivedAt) {}

    public static ProductResponse from(Product p) {
        List<DocInfo> docs = p.getDocuments() == null ? List.of() :
            p.getDocuments().stream().map(d -> new DocInfo(
                d.getProductDocumentId(),
                d.getDocType() != null ? d.getDocType().name() : "",
                d.getTitle(),
                d.getNote(),
                d.getFilename(),
                DateUtil.format(d.getSubmittedAt()),
                DateUtil.format(d.getReceivedAt())
            )).collect(Collectors.toList());
        return new ProductResponse(
                p.getProductId(),
                p.getProductId(),
                p.getProductCode(),
                p.getProductName(),
                p.getStatus() != null ? p.getStatus().name() : "",
                p.getStatusLabel(),
                p.getTargetDescription(),
                DateUtil.format(p.getSaleStartDate()),
                DateUtil.format(p.getSaleEndDate()),
                p.getDescription(),
                docs
        );
    }
}
