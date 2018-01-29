package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @PostMapping("")
    public String login(UserDto user, HttpSession session) {
        try {
            User login = userService.login(user.getUserId(), user.getPassword());
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, login);
            return "redirect:/users";
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
    }
}
