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
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/form")
    public String loginForm() {
        return "/user/login";
    }

    @PostMapping
    public String login(String userId, String password, HttpSession session) {
        try {
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userId, password));
            return "redirect:/";
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
    }

}
