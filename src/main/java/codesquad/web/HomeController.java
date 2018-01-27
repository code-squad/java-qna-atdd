package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.dto.LoginUserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;


    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Model model){
        return "/user/login";
    }

    @PostMapping("/login")
    public String login(LoginUserDto loginUserDto, HttpSession session) throws UnAuthenticationException {
        Optional<User> user = Optional.ofNullable(userService.login(loginUserDto.getUserId(), loginUserDto.getPassword()));
        if (user.isPresent()){
            session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
            return "redirect:/";
        }
        return "/user/login_failed";
    }
}
