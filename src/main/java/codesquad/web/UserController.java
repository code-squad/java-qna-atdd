package codesquad.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserRepository userRepository;

	@Resource(name = "userService")
	private UserService userService;

	@GetMapping("/form")
	public String form() {
		return "/user/form";
	}

	@PostMapping("/login")
	public String login(String userId, String password, HttpSession session) {
		try {
			User loginUser = userService.login(userId, password);
			session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
		} catch (UnAuthenticationException e) {
			return "/user/login_failed";
		}
		return "redirect:/users";
	}

	@PostMapping("")
	public String create(UserDto userDto) {
		userService.add(userDto);
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
	public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
		model.addAttribute("user", userService.findById(loginUser, id));
		return "/user/updateForm";
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, UserDto target) {
		userService.update(loginUser, id, target);
		return "redirect:/users";
	}

}
