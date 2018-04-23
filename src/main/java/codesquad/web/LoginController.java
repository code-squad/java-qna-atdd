package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author sangsik.kim
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Resource(name = "userService")
    private UserService userService;
    
    @GetMapping
    public String loginForm() {
        return "/user/login";
    }

    @PostMapping
    public String login(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        User loginUser = userService.login(userId, password);
        httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
        return "redirect:/users";
    }
}
