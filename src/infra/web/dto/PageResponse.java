package infra.web.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        int total,
        int page,
        int limit,
        int totalPages
) {
    public static <T> PageResponse<T> of(List<T> data, int total, PageRequest req) {
        int totalPages = (int) Math.ceil((double) total / req.limit());
        return new PageResponse<>(data, total, req.page(), req.limit(), totalPages);
    }
}
