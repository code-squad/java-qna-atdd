package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.dto.UserDto;
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
    private static final String REDIRECT_USERS = "redirect:/users";

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping("/login")
    public String loginForm() {
        return "/user/login";
    }

    @GetMapping("/form")
    public String form() {
        return "/user/form";
    }

    @PostMapping("")
    public String create(UserDto userDto) {
        userService.add(userDto);
        return REDIRECT_USERS;
    }

    @GetMapping("")
    public String list(Model model) {
        List<User> users = userService.findAll();
        log.debug("user size : {}", users.size());
        model.addAttribute("users", users);
        return "/user/list";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        model.addAttribute("user", userService.findById(loginUser, id));
        return "/user/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, UserDto target) {
        userService.update(loginUser, id, target);
        return REDIRECT_USERS;
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession httpSession) {
        User newUser = null;
        try {
            newUser = userService.login(userId, password);
        } catch (UnAuthenticationException e) {
            log.info(e.getMessage());
            return "redirect:/user/login_failed";
        }

        httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, newUser);

        return REDIRECT_USERS;
    }
}
