package codesquad;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "에러가 발생하였습니다";

    private String message;

    private Object[] arguments;

    CustomException(String message) {
        super(message);
        this.message = message;
    }

    CustomException(String message, Object... arguments) {
        super(message);
        this.message = message;
        this.arguments = arguments;
    }

    public abstract HttpStatus status();

    @Override
    public String getMessage() {
        return String.format("%s %s", getErrorMessage(), status());
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getMessageCode() {
        return this.getClass().getSimpleName();
    }

    private String getErrorMessage() {
        if (StringUtils.isEmpty(message)) {
            return DEFAULT_MESSAGE;
        }
        return message;
    }
}
