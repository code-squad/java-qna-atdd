package codesquad.security;

import javax.persistence.EntityNotFoundException;

import codesquad.CannotUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;

@ControllerAdvice
public class SecurityControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(SecurityControllerAdvice.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void emptyResultData(EntityNotFoundException exception) {
        log.debug("EntityNotFoundException is happened!");
        log.debug("persistence error : {}", exception.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void unAuthorized() {
        log.debug("UnAuthorizedException is happened!");
    }
    
    @ExceptionHandler(UnAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public void unAuthentication() {
        log.debug("UnAuthenticationException is happened!");
    }
    
    @ExceptionHandler({CannotUpdateException.class, CannotUpdateException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void CannotUpdateOrDeleteException(Exception exception) {
        log.debug(exception.getClass().getName() + " is happened!");
        log.debug("persistence error : {}", exception.getMessage());
    }
}
