package codesquad;

public class CannotDeleteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CannotDeleteException() {
        super();
    }

    public CannotDeleteException(String message) {
        super(message);
    }

    public CannotDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotDeleteException(Throwable cause) {
        super(cause);
    }
}
