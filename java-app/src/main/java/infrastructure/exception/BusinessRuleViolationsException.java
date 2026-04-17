package infrastructure.exception;

public class BusinessRuleViolationsException extends RuntimeException {
    public BusinessRuleViolationsException(String message) {
        super(message);
    }
}
