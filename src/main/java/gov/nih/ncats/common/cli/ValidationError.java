package gov.nih.ncats.common.cli;

/**
 * Created by katzelda on 6/22/17.
 */
public class ValidationError extends RuntimeException {
    public ValidationError(Throwable throwable) {
        super(throwable);
    }


    public ValidationError(String message) {
        super(message);
    }
}
