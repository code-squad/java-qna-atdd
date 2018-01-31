package codesquad;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "에러가 발생하였습니다";

    private String message;

    CustomException() {

    }

    CustomException(String message) {
        super(message);
        this.message = message;
    }

    abstract HttpStatus status();

    @Override
    public String getMessage() {
        return String.format("%s %s", getErrorMessage(), status());
    }

    private String getErrorMessage() {
        if (StringUtils.isEmpty(message)) {
            return DEFAULT_MESSAGE;
        }
        return message;
    }

}
