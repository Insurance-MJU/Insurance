package domain.exception;

import java.util.Collections;
import java.util.List;

public class ValidationException extends DomainException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = Collections.unmodifiableList(errors);
    }

    public ValidationException(String error) {
        super(error);
        this.errors = Collections.singletonList(error);
    }

    public List<String> getErrors() {
        return errors;
    }
}
