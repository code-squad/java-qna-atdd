package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/form")
    public String form() {
        return "/user/form";
    }

    @PostMapping("")
    public String create(User user) {
        userService.add(user);
        return "redirect:/users";
    }

    @GetMapping("")
    public String list(Model model) {
        List<User> users = userService.findAll();
        log.debug("user size : {}", users.size());
        model.addAttribute("users", users);
        return "/user/list";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {   //401에러 뜸 (unauthorized)
        model.addAttribute("user", userService.findById(loginUser, id));
        return "/user/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, User target) {
        userService.update(loginUser, id, target);
        return "redirect:/users";
    }

    @GetMapping("/login/form")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {
        try {
            User user = userService.login(userId, password);
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
            return "redirect:/";
        } catch (UnAuthenticationException e) {
            return "user/login_failed";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
        return "redirect:/";
    }
}
