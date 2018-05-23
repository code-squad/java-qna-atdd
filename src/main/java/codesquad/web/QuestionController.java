package codesquad.web;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;

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
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

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
	public String create(@LoginUser User loginUser, QuestionDto questionDto) {
		if(loginUser.isGuestUser()) {
			return "/user/login_failed";
		}
		qnaService.create(loginUser, questionDto.toQuestion());
		return "redirect:/questions";
	}
	
	@GetMapping("/{id}")
	public String showQuestion(@PathVariable Long id, Model model) {
		model.addAttribute("question", qnaService.findById(id).get());
		return "/qna/show";
	}
	
	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
		if(loginUser.isGuestUser()) {
			return "/user/login_failed";
		}
		try {
			Question question = qnaService.findById(id).filter(q -> q.isOwner(loginUser)).orElseThrow(AuthenticationException::new);
			model.addAttribute("question", question);
			return "/qna/updateForm";
		} catch (AuthenticationException e) {
			model.addAttribute("errorMessage", "자기 자신의 글만 수정 가능");
			return "/user/login_failed";
		}
	}
	

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable Long id, QuestionDto updateQuestion) {
		try {
			qnaService.update(loginUser, id, updateQuestion.toQuestion());
		} catch (AuthenticationException e) {
			return "user/login_failed";
		}
		return String.format("redirect:/questions/%d", id);
	}
	
	@DeleteMapping("/{id}")
	public String delete(@LoginUser User loginUser, @PathVariable Long id) {
		try {
			qnaService.deleteQuestion(loginUser, id);
		} catch (CannotDeleteException e) {
			return "user/login_failed";
		}
		return "redirect:/";
	}
}
