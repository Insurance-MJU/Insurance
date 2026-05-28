package common.exception.infra;

public class BadRequestException extends InfraException {
    public BadRequestException(String message) { super(400, message); }
}
