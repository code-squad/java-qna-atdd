package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("")
    public String login() {
        return "/user/login";
    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession httpSession) {
        try {
            httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userId, password));
            return "redirect:/users";
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
    }
}
