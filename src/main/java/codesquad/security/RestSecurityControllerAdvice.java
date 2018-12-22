package codesquad.security;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthenticationException;
import codesquad.exception.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import support.domain.ErrorMessage;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

@RestControllerAdvice(annotations = RestController.class)
public class RestSecurityControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(RestSecurityControllerAdvice.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void emptyResultData() {
        log.debug("EntityNotFoundException is happened!");
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public void unAuthorized() {
        log.debug("UnAuthorizedException is happened!");
    }

    @ExceptionHandler(UnAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorMessage unAuthentication(UnAuthenticationException e) {
        log.debug("JSON API UnAuthenticationException is happened!");
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(CannotDeleteException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ErrorMessage cannotDelete(CannotDeleteException e) {
        log.debug("JSON API CannotDeleteException is happened!");
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(NoResultException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void noResult() {
        log.debug("NoResultException is happened!");
    }
}
