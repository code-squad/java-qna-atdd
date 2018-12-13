package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")

public class LoginController {
    private static final Logger log = LogManager.getLogger(LoginController.class);

    @Resource(name = "userService")
    private UserService userService;


    @GetMapping("")
    public String loginForm() {
        return "/user/login";
    }

    @PostMapping("")
    public String loginUser(String userId, String password, HttpSession session) {
        try {
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY,userService.login(userId,password));
            return "redirect:/";
        } catch (Exception e) {
            return "/user/login_failed";
        }
    }

}
