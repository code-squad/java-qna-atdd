package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/form")
    public String loginForm() {
        return "/user/login";
    }

    @GetMapping("/loginFail")
    public String loginFail() {
        log.debug("################### loginFail");
        return "/user/login_failed";
    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession session) {
        try {
            User loginUser = userService.login(userId, password);
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
            return "redirect:/users";
        } catch (UnAuthenticationException e) {
            //return "redirect:/login/loginFail";
            return "/user/login_failed";
        }
    }
}
