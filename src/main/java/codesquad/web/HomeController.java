package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.dto.UserDto;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

import static codesquad.security.HttpSessionUtils.USER_SESSION_KEY;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(HttpSession session, UserDto user) throws UnAuthenticationException {
        session.setAttribute(USER_SESSION_KEY, userService.login(user.getUserId(), user.getPassword()));
        return "redirect:/";
    }
}
