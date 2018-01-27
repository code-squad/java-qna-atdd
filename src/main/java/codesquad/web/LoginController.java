package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "user/login";
    }

    // TODO : 상태를 변경하는 것이므로 POST 로 바꿔보기 (html 수정 필요)
    @GetMapping("/logout")
    public String logoutPage(HttpSession httpSession) {
        // [NEED TO STUDY] : httpSession.invalidate()
        httpSession.invalidate();
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(HttpSession httpSession, String userId, String password) {
        try {
            User user = userService.login(userId, password);
            // [NEED TO STUDY] : Session
            httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
            log.debug("user=" + user);
        } catch (UnAuthenticationException e) {
            return "user/login_failed";
        }

        // [NEED TO STUDY] : Redirect
        return "redirect:/users";
    }
}
