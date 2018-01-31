package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {
        try {
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userId, password));
            return "redirect:/users";
        } catch (UnAuthenticationException e) {
            return "redirect:/login_failed";
        }
    }

    @GetMapping("/login_failed")
    public String loginFailed() {
        return "user/login_failed";
    }
}
