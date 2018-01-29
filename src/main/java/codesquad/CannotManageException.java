package codesquad;

public class CannotManageException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotManageException(String message) {
        super(message);
    }
}