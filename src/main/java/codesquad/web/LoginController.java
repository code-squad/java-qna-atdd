package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login/form")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {
        try{
            User loginUser = userService.login(userId, password);
            session.setAttribute("loginUser", loginUser);
            return "redirect:/";
        }catch (UnAuthenticationException e) {
            return "user/login_failed";
        }
    }
}
