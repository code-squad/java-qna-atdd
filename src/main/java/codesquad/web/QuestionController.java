package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@GetMapping("")
	public String showAll(Model model) {
		model.addAttribute("questions", qnaService.findAll());
		return "home";
	}

	@GetMapping("/form")
	public String form() {
		return "/qna/form";
	}

	@PostMapping("")
	public String create(@LoginUser User user, String title, String contents) {
		Question question = new Question(title, contents);
		qnaService.create(user, question);
		return "redirect:/questions";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/show";
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User user, @PathVariable long id, String title, String contents) {
		Question target = new Question(title, contents);
		qnaService.update(user, id, target);
		return "redirect:/questions";
	}

	@DeleteMapping("/{id}")
	public String delete(@LoginUser User user, @PathVariable long id) {
		try {
			qnaService.deleteQuestion(user, id);
		} catch (CannotDeleteException e) {
			e.printStackTrace();
		}
		return "redirect:/questions";
	}

}
