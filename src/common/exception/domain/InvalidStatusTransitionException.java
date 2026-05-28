package common.exception.domain;

public class InvalidStatusTransitionException extends DomainException {
    public InvalidStatusTransitionException(String current, String attempted) {
        super(409, "상태 전이 불가: [" + current + "] → [" + attempted + "]");
    }
}
