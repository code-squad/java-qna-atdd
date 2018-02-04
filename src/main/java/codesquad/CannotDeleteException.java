package codesquad;

import org.springframework.http.HttpStatus;

public class CannotDeleteException extends CustomException {
    private static final long serialVersionUID = 1L;

    public CannotDeleteException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.FORBIDDEN;
    }
}