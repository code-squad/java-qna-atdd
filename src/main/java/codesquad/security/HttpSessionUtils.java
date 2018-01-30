package codesquad.security;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import codesquad.domain.User;

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
        Object sessionedUser = session.getAttribute(USER_SESSION_KEY);
        if (sessionedUser == null) {
            return false;
        }
        return true;
    }

    public static User getUserFromSession(HttpSession session) {
        if (!isLoginUser(session)) {
            return null;
        }

        return (User) session.getAttribute(USER_SESSION_KEY);
    }

    public static void setUserInSession(HttpSession session, User user) {
        if(user.isGuestUser())
            return;

        session.setAttribute(USER_SESSION_KEY, user);
    }

    public static void logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
    }
}
