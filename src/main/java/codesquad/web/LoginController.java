package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Resource(name = "userService")
    private UserService userService;

    @PostMapping("/login")
    public String login(UserDto user, HttpSession session) {
        try {
            User loginUser = userService.login(user.getUserId(), user.getPassword());

            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
            return "redirect:/users";
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }
}
