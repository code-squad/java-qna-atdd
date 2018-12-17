package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

import java.util.Enumeration;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger logger = getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @GetMapping()
    public String loginForm() {
        return "/user/login";
    }

    @PostMapping()
    public String login(String userId, String password, HttpSession httpSession) throws UnAuthenticationException {
        httpSession.setAttribute(HttpSessionUtils.USER_SESSION_KEY, userService.login(userId, password));
        logger.debug("httpSession.getAttribute : {}", httpSession.getAttribute(HttpSessionUtils.USER_SESSION_KEY));

        Enumeration sessionAttributeNames = httpSession.getAttributeNames();

        while(sessionAttributeNames.hasMoreElements()) {
            String sessionName = sessionAttributeNames.nextElement()+"";
            logger.debug("session : {} : {}", sessionName, httpSession.getAttribute(sessionName));
        }

        return "redirect:/";
    }
}
