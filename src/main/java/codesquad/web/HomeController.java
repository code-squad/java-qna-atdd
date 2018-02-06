package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class HomeController {

    private final UserService userService;
    private final QnaService qnaService;

    @Autowired
    public HomeController(UserService userService, QnaService qnaService) {
        this.userService = userService;
        this.qnaService = qnaService;
    }

    @GetMapping("/")
    public String home(Model model, @PageableDefault Pageable pageable) {
        model.addAttribute("questions", qnaService.findAll(pageable));
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

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String doLogout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }
}
