package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginForm () {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(UserDto userDto, HttpSession session) {
        try {
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userDto.getUserId(), userDto.getPassword()));;
        } catch (UnAuthenticationException e ) {
            return "user/login_failed";
        }
        return "redirect:/users";
    }

    @GetMapping("/logout")
    public String logout (HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
