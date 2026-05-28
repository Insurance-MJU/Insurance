package common.exception.domain;

import java.util.Collections;
import java.util.List;

public class ValidationException extends DomainException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(400, String.join(", ", errors));
        this.errors = Collections.unmodifiableList(errors);
    }

    public ValidationException(String error) {
        super(400, error);
        this.errors = Collections.singletonList(error);
    }

    public List<String> getErrors() {
        return errors;
    }
}
