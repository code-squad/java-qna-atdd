package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String doLogin(HttpServletRequest request, String userId, String password) {
        User user;
        try {
            user = userService.login(userId, password);
        } catch (UnAuthenticationException e) {
            return "user/login_failed";
        }
        request.getSession().setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);

        return "redirect:/users";
    }

    @GetMapping("/logout")
    public String doLogout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }
}
