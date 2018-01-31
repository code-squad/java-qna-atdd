package codesquad.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import codesquad.CannotManageException;
import lombok.extern.log4j.Log4j;

@Log4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(CannotManageException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void unAuthorized() { log.debug("UnAuthorizedException is happened!"); }
}
