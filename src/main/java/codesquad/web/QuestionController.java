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
import codesquad.security.BasicAuthInterceptor;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@Controller
@RequestMapping("/questions")
public class QuestionController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public String create(@LoginUser User loginUser, QuestionDto question, Model model, HttpSession session) {
		checkAuth(loginUser, session);
		qnaService.create(loginUser, question);
		return "redirect:/";
	}

	@GetMapping("/{id}")
	public String showDetail(User user,@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "/qna/show";
	}

	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model, HttpSession session) {
		checkAuth(loginUser, session);
		Question question = qnaService.findById(id);
		model.addAttribute("question", question);
		return "/qna/updateForm";
	}

	@PutMapping("/{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto updatequestion,
			HttpSession session) {
		checkAuth(loginUser, session);
		qnaService.update(loginUser, id, updatequestion);
		return String.format("redirect:/questions/%d", id);
	}

	@DeleteMapping("/{id}")
	public String delete(@LoginUser User loginUser, @PathVariable long id, HttpSession session)
			throws CannotDeleteException {
		checkAuth(loginUser, session);
		qnaService.deleteQuestion(loginUser, id);
		return "redirect:/";
	}

	public String checkAuth(@LoginUser User loginUser, HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session))
			return "/user/login";
		if (!loginUser.equals(session.getAttribute(HttpSessionUtils.USER_SESSION_KEY)))
			throw new IllegalStateException("자신의 게시물만 수정/삭제 가능합니다");
		return "success";
	}

}
