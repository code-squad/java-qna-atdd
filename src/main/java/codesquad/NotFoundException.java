package codesquad;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    NotFoundException(String message) {
        super(message);
    }

    NotFoundException(String message, Object... arguments) {
        super(message, arguments);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }

}
