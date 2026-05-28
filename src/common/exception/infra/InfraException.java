package common.exception.infra;

public abstract class InfraException extends RuntimeException {
    private final int status;

    public InfraException(int status, String message) {
        super(message);
        this.status = status;
    }

    public InfraException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public int getStatus() { return status; }
}
