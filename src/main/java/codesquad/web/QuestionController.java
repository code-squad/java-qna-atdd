package codesquad.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource(name = "userController")
	private UserController userController;
	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@GetMapping("")
	public String questionsHome() {
		return "/home";
	}
	
	@GetMapping("/form")
	public String form() {
		return "/qna/form";
	}
	
	@PostMapping("/create")
	public String create(@LoginUser User loginUser, QuestionDto target, HttpSession session) {
		Question newQuestion = qnaService.create(loginUser, new Question(target.getTitle(), target.getContents()));
		return "redirect:/";
	}
	
	@GetMapping("/{id}")
	public String showQuestion(@PathVariable Long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/show";
	}
	
	@GetMapping("/{id}/updateFail")
	public String updateFail(@PathVariable Long id, Model model) {
		model.addAttribute("errorMessage", "수정 권한이 없습니다.");
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/updateFail";
	}
	
	@GetMapping("/{id}/form")
	public String updateForm(@PathVariable Long id, @LoginUser User loginUser, Model model) {
		Question question = qnaService.findById(id);
		if (!question.isOwner(loginUser)) {
			log.debug("본인의 글만 수정할 수 있습니다.");
			return "redirect:/questions/{id}/updateFail";
		}
		
		model.addAttribute("question", question);
		return "/qna/updateForm";
	}
	
	@PutMapping("/{id}")
	public String update(@PathVariable Long id, @LoginUser User loginUser, String title, String contents, Model model) throws CannotDeleteException {
		Question question = qnaService.findById(id);
		if (!question.isOwner(loginUser)) {
			log.debug("권한이 없습니다.");
			return "redirect:/questions/{id}/updateFail";
		}
		question.update(title, contents);
		question = qnaService.update(loginUser, id, question);
		model.addAttribute("question", question);
		return "redirect:/questions/{id}";
	}
	
	@DeleteMapping("/{id}")
	public String delete(@PathVariable Long id, @LoginUser User loginUser) throws CannotDeleteException {
		Question question = qnaService.findById(id);
		if (!question.isOwner(loginUser)) {
			log.debug("권한이 없습니다.");
			return "redirect:/questions/{id}/updateFail";
		}
		qnaService.deleteQuestion(loginUser, id);
		return "redirect:/";
	}
}
