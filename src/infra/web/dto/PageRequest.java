package infra.web.dto;

public record PageRequest(int page, int limit) {

    private static final int MAX_LIMIT = 50;

    public static PageRequest from(HttpRequest req) {
        int page  = parseOrDefault(req.queryParam("page"),  1);
        int limit = parseOrDefault(req.queryParam("limit"), 10);
        if (page < 1)          throw new ValidationException("page는 1 이상이어야 합니다.");
        if (limit < 1)         throw new ValidationException("limit는 1 이상이어야 합니다.");
        if (limit > MAX_LIMIT) throw new ValidationException("limit는 최대 " + MAX_LIMIT + "까지 허용됩니다.");
        return new PageRequest(page, limit);
    }

    public int offset() {
        return (page - 1) * limit;
    }

    private static int parseOrDefault(String value, int defaultValue) {
        if (value == null) return defaultValue;
        try { return Integer.parseInt(value); } catch (NumberFormatException e) { return defaultValue; }
    }
}
