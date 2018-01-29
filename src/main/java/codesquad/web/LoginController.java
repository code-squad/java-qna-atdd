package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.dto.UserDto;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

import static codesquad.security.HttpSessionUtils.USER_SESSION_KEY;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(UserDto user, HttpSession session) {
        try {
            session.setAttribute(USER_SESSION_KEY, userService.login(user.getUserId(), user.getPassword()));
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
        return "redirect:/";
    }
}
