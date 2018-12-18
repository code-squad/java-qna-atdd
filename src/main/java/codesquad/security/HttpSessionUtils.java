package codesquad.security;

import codesquad.domain.User;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;

//여기에 있는 메소드들을 @LoginUser 에서 쓰는듯 하다. 컨트롤러에서 login메소드에서 USER_SESSION_KEY와 다른 값으로 세션 네임을 지정하면, 401에러 발생 (로그인 인증이 안되는듯)
public class HttpSessionUtils {
    public static final String USER_SESSION_KEY = "loginUser";

    public static boolean isLoginUser(NativeWebRequest webRequest) {
        Object loginUser = webRequest.getAttribute(USER_SESSION_KEY, WebRequest.SCOPE_SESSION);
        return loginUser != null;
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
}
