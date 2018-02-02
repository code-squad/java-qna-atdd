package codesquad.web;

import codesquad.CustomException;
import codesquad.NotFoundException;
import codesquad.UnAuthorizedException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(basePackages = "codesquad.web",
                  annotations = Controller.class)
@Order(1)
public class WebControllerAdvice {

    private final MessageSourceAccessor accessor;

    public WebControllerAdvice(MessageSourceAccessor accessor) {
        this.accessor = accessor;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFoundException(NotFoundException ex) {
        return createView(getCustomErrorMessage(ex));
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleUnAuthorizedException() {
        return createView("해당 권한이 없습니다");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleInternalServerError(RuntimeException ex) {
        return createView(ex.getLocalizedMessage());
    }

    private ModelAndView createView(String errorMessage) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("message", errorMessage);

        return mv;
    }

    private String getCustomErrorMessage(CustomException e) {
        return accessor.getMessage(e.getMessageCode(), e.getArguments(), e.getMessage());
    }
}