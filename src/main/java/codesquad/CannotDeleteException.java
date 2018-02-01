package codesquad;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CannotDeleteException extends Exception {
    private static final long serialVersionUID = 1L;

    public CannotDeleteException(String message) {
        super(message);
    }
}