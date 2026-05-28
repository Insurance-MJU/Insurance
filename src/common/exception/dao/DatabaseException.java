package common.exception.dao;

public class DatabaseException extends DataAccessException {
    public DatabaseException(String message, Throwable cause) {
        super(500, message, cause);
    }
}
