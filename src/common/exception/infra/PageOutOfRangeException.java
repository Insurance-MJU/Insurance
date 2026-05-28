package common.exception.infra;

public class PageOutOfRangeException extends InfraException {

    public PageOutOfRangeException(int page, int totalPages) {
        super(400, "Page " + page + " out of range. Total pages: " + totalPages);
    }
}
