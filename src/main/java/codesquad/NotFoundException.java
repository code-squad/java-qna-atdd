package codesquad;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomException {

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }

}
