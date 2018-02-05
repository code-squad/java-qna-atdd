package codesquad;

public class CannotFindException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotFindException(String message) {
        super(message);
    }
}
