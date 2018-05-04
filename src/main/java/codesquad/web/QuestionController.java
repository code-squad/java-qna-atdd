package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping(QuestionController.BASE_URL)
public class QuestionController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	static final String BASE_URL = "/questions";
	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@GetMapping("/form")
	public String form() {
		return "/qna/form";
	}

	@PostMapping("")
	public String create(@LoginUser User loginUser, String title, String contents) {
		Question question = qnaService.createQuestion(loginUser, new Question(title, contents));
		return "redirect:" + BASE_URL + "/" + question.getId();
	}

	@GetMapping("/{id}")
	public String show(@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findQuestionById(id));
		return "/qna/show";
	}

	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
		Question question = qnaService.findQuestionById(id);
		if (question.isOwner(loginUser)) {
			model.addAttribute("question", question);
			return "/qna/question_updateForm";
		}
		return "/qna/update_failed";
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto target) {
		try {
			qnaService.updateQuestion(loginUser, id, target.toQuesiton());
			return "redirect:/questions/" + id;
		} catch (CannotUpdateException e) {
			return "/qna/update_failed";
		}
	}

	@DeleteMapping("/{id}")
	public String delete(@LoginUser User loginUser, @PathVariable long id) {
		try {
			qnaService.deleteQuestion(loginUser, id);
			return "redirect:/";
		} catch (CannotDeleteException e) {
			return "/qna/delete_failed";
		}
	}
	
	@PostMapping("/{id}/answers")
	public String addAnswer(@LoginUser User loginUser, @PathVariable long id, String answerContents) {
		qnaService.addAnswer(loginUser, id, answerContents);
		return "redirect:" + BASE_URL + "/" + id;
	}
}
