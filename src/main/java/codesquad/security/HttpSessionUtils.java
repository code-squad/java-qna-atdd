package codesquad.security;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import codesquad.domain.User;

import java.util.Optional;

public class HttpSessionUtils {
    public static final String USER_SESSION_KEY = "loginedUser";

    public static boolean isLoginUser(NativeWebRequest webRequest) {
        Object loginedUser = webRequest.getAttribute(USER_SESSION_KEY, WebRequest.SCOPE_SESSION);
        return loginedUser != null;
    }

    public static User getUserFromSession(NativeWebRequest webRequest) {
        if (!isLoginUser(webRequest)) {
            return User.GUEST_USER;
        }
        return (User) webRequest.getAttribute(USER_SESSION_KEY, WebRequest.SCOPE_SESSION);
    }

    public static boolean isLoginUser(HttpSession session) {
        return Optional.ofNullable(session.getAttribute(USER_SESSION_KEY)).isPresent();
    }

    public static User getUserFromSession(HttpSession session) {
        if (!isLoginUser(session)) {
            return null;
        }

        return (User) session.getAttribute(USER_SESSION_KEY);
    }
}
