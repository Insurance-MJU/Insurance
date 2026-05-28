package common.exception.domain;

public abstract class DomainException extends RuntimeException {
    private final int status;

    public DomainException(int status, String message) {
        super(message);
        this.status = status;
    }

    public DomainException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() { return status; }
}
