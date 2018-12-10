package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
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

    @GetMapping("")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession session) {
        try {
            User loginUser = userService.login(userId, password);
            session.setAttribute("loginUser", loginUser);
        } catch (UnAuthenticationException e) {
            return "user/login_failed";
        }
        return "redirect:/";
    }
}
