package common.exception.infra;

public class ForbiddenException extends InfraException {
    public ForbiddenException(String message) { super(403, message); }
}
