package common.exception.infra;

public class NotFoundException extends InfraException {
    public NotFoundException(String message) { super(404, message); }
}
