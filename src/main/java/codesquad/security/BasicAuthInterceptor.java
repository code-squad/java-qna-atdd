package codesquad.security;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Base64;
/* HandlerInterceptorAdapter 상속받거나, HandlerInterceptor 인터페이스를 구현하는 방식으로 사용 가능! */
public class BasicAuthInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(BasicAuthInterceptor.class);

    @Autowired
    private UserService userService;

    /*
        컨트롤러가 호출 전 실행 (Request)
        매개변수 obj는 Dispatcher의 HandlerMapping이 찾아준 컨트롤러의 메서드를 참조할 수 있는 HandlerMethod 객체
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String authorization = request.getHeader("Authorization");
        log.debug("Authorization : {}", authorization);
        if (authorization == null || !authorization.startsWith("Basic")) {
            return true;
        }

        String base64Credentials = authorization.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
        final String[] values = credentials.split(":", 2);
        log.debug("username : {}", values[0]);
        log.debug("password : {}", values[1]);

        try {
            User user = userService.login(values[0], values[1]);
            log.debug("Login Success : {}", user);
            request.getSession().setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
            return true;
        } catch (UnAuthenticationException e) {
            return true;
        }
    }

    /* postHandle 컨트롤러 호출 전 실행 (Response) */

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
    /* 질문) 인터셉터, 필터, aop, contentresolver 차이 */
}
