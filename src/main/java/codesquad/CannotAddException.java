package codesquad;

public class CannotAddException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotAddException(String message) {
        super(message);
    }
}