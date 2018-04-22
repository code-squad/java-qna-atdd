package codesquad;

import codesquad.domain.CannotDeleteException;
import codesquad.domain.CannotUpdateException;
import codesquad.domain.UnAuthenticationException;
import codesquad.domain.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = UnAuthenticationException.class)
    @ResponseStatus(HttpStatus.FOUND)
    public String handleUnAuthentication() {
        return "redirect:/user/login_failed";
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleUnAuthorized() {
    }

    @ExceptionHandler(value = {CannotUpdateException.class, CannotDeleteException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleCannotDelete() {
        return "redirect:/";
    }
}
