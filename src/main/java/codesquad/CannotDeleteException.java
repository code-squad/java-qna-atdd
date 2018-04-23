package codesquad;

public class CannotDeleteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CannotDeleteException() {
        super();
    }

    public CannotDeleteException(String message) {
        super(message);
    }
}