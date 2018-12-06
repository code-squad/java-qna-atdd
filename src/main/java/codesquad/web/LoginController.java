package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return "/user/login";
    }

//    @GetMapping("/failed")
//    public String failForm() {
//        return "/user/login_failed";
//    }

    @PostMapping("")
    public String login(String userId, String password, HttpSession httpSession) {
        // TODO 로그인 기능 구현 및 세션에 User 정보 저장
        try {
        User user = userService.login(userId, password);
        httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
        return "redirect:/";
        } catch (UnAuthenticationException e) {
            return "/user/login_failed";
        }
    }
}