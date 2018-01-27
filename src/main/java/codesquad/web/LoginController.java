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
    private UserService userService;

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {
        log.debug("userId : {}, password : {}", userId, password);
        try {
            User loginUser = userService.login(userId, password);
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
        return "redirect:/users";
    }
}
