package common.exception.dao;

public abstract class DataAccessException extends RuntimeException {
    private final int status;

    public DataAccessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public DataAccessException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() { return status; }
}
