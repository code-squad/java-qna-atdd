package codesquad.web;

import java.util.List;
import java.util.Optional;

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

	@Resource(name = "userService")
	private UserService userService;

	@GetMapping("/form")
	public String form() {
		return "/user/form";
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

	@GetMapping("/loginForm")
	public String loginForm() {
		return "/user/login";
	}

	@GetMapping("/loginFailed")
	public String loginFailed(Model model) {
		model.addAttribute("errorMessage", "아이디와 비밀번호를 확인해주세요.");
		return "/user/login_failed";
	}

	@PostMapping("/login")
	public String login(UserDto target, HttpSession session) throws UnAuthenticationException {
		System.out.println("login ID : " + target.getUserId() + " | login PASSWORD : " + target.getPassword());
		try {
			User loginUser = userService.login(target.getUserId(), target.getPassword());
			session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, loginUser);
		}catch (UnAuthenticationException e) {
			log.debug("=============================== <ERROR> : login error ===============================");
			return "redirect:/users/loginFailed";
		}
		return "redirect:/users";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
		log.debug("======== Success to LOGOUT!! ========");

		return "redirect:/";
	}
}
