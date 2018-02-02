package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
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
@RequestMapping("/questions")
public class QuestionController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@GetMapping("/form")
	public String form() {
		return "/qna/form";
	}

	@PostMapping("")
	public String create(@LoginUser User loginUser, String title, String contents) {
		Question question = new Question(title, contents);
		qnaService.create(loginUser, question);
		return "redirect:/questions";
	}

	@GetMapping("")
	public String list(Model model) {
		model.addAttribute("questions", qnaService.findAll());
		return "/home";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/show";
	}

	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/updateForm";
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, String title, String contents) {
		QuestionDto target = new QuestionDto(title, contents);
		qnaService.update(loginUser, id, target);
		return String.format("redirect:/questions/%d", id);
	}

	@DeleteMapping("/{id}")
	public String delete(@LoginUser User loginUser, @PathVariable long id) {
		try {
			qnaService.deleteQuestion(loginUser, id);
			return "redirect:/questions";
		} catch (CannotDeleteException e) {
			return String.format("redirect:/questions/%d", id);
		}
	}

}
