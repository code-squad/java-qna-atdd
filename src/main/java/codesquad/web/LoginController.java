package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @GetMapping("/login/form")
    public String loginForm(){
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(User user) {
        try {
            userService.login(user.getUserId(), user.getPassword());
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
        return "redirect:/";
    }
}
