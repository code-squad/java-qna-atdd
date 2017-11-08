package codesquad.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class LoginRequiredAdvice {
    private static final Logger log = LoggerFactory.getLogger(LoginRequiredAdvice.class);

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public void unAuthorized() {
        log.debug("UnAuthorizedException is happened!");
    }
}
