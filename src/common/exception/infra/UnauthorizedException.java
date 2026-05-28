package common.exception.infra;

public class UnauthorizedException extends InfraException {
    public UnauthorizedException(String message) { super(401, message); }
}
