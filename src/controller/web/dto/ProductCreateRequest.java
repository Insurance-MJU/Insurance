package controller.web.dto;

import java.util.List;

public record ProductCreateRequest(
        String productCode,
        String productName,
        String description,
        String target,
        String saleStartDate,
        String saleEndDate,
        List<String> riderCodes,
        List<String> coverageIds
) {}
