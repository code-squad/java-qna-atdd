package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
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
    public String loginForm() {

        return "user/login";
    }

    @PostMapping("/login")
    public String loginForm(String userId, String password,HttpSession session) {
        try {
            User loginUser = userService.login(userId,password);
            session.setAttribute("loginedUser",loginUser);
        } catch (UnAuthenticationException e) {
            return "user/login_failed";
        }
        return "redirect:/";
    }
}
